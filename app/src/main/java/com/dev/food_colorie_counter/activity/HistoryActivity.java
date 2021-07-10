package com.dev.food_colorie_counter.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.food_colorie_counter.R;
import com.dev.food_colorie_counter.adapters.HistoryAdapter;
import com.dev.food_colorie_counter.utils.History;
import com.dev.food_colorie_counter.utils.ImageUtil;
import com.dev.food_colorie_counter.utils.LoadingDialog;
import com.dev.food_colorie_counter.utils.Preferences;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HistoryActivity extends AppCompatActivity implements View.OnClickListener, HistoryAdapter.ItemClickListener {

    private static final String TAG = HistoryActivity.class.getName();
    private LoadingDialog loadingDialog;

    TextView txt_date;
    ImageView btn_prev_date, btn_next_date;

    RecyclerView recycler_history;
    FirebaseFirestore db;

    ArrayList<History> historyList;
    HistoryAdapter historyAdapter;

    int counter = 0;

    String mCurrentPhotoPath = "";
    Uri photoURI;
    String imageFileName = "";
    File myImage = null;
    Uri downUri;
    String imgurl = "";

    static final int REQUEST_IMAGE_CAPTURE = 1;
    StorageReference storageRef = FirebaseStorage.getInstance().getReference("Food_Images");

    private static String MY_TOKEN = null;

    Double calorie_plan = 0.0;
    Double rate = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        initUI();
        db = FirebaseFirestore.getInstance();

        txt_date.setText(Preferences.getValue_String(HistoryActivity.this, Preferences.today_date));

        getAllData(Preferences.getValue_String(HistoryActivity.this, Preferences.today_date));
    }

    private void initUI(){
        recycler_history = findViewById(R.id.recycler_history);

        txt_date = findViewById(R.id.txt_date);

        btn_prev_date = findViewById(R.id.btn_prev_date);
        btn_next_date = findViewById(R.id.btn_next_date);

        btn_prev_date.setOnClickListener(this);
        btn_next_date.setOnClickListener(this);

        MY_TOKEN = getString(R.string.caloriemama_token);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.btn_prev_date:
                counter--;
                getSearchDate(counter);
                break;

            case R.id.btn_next_date:
                counter++;
                getSearchDate(counter);
                break;

            default:
                break;
        }
    }

    private void getSearchDate(int counter) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, counter);
        txt_date.setText(dateFormat.format(cal.getTime()));
        getAllData(dateFormat.format(cal.getTime()));
    }

    private void getAllData(String today){
        loadingDialog = new LoadingDialog(HistoryActivity.this, false);
        db.collection("food_informations/" + Preferences.getValue_String(HistoryActivity.this, Preferences.user_ID) + "/" + today)
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        historyList = new ArrayList<History>();
                        recycler_history.setLayoutManager(new LinearLayoutManager(HistoryActivity.this));
                        recycler_history.setItemAnimator(new DefaultItemAnimator());

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());

                            historyList.add(new History(
                                    document.getId(),
                                    document.getData().get("imgurl").toString(),
                                    document.getData().get("time").toString(),
                                    document.getData().get("calories").toString(),
                                    document.getData().get("foodname").toString(),
                                    document.getData().get("foodgroup").toString(),
                                    document.getData().get("imgurl_remain").toString(),
                                    document.getData().get("calories_remain").toString(),
                                    document.getData().get("servingWeight").toString()
                            ));
                        }

                        historyAdapter = new HistoryAdapter(HistoryActivity.this, historyList, HistoryActivity.this);
                        recycler_history.setAdapter(historyAdapter);
                        loadingDialog.hide();
                    } else {
                        loadingDialog.hide();
                    }
                }
            });
    }

    private void getFoodInfo(final String food_ID) {
        loadingDialog = new LoadingDialog(HistoryActivity.this, false);
        db.collection("food_informations/" + Preferences.getValue_String(HistoryActivity.this, Preferences.user_ID) + "/" + txt_date.getText().toString())
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());

                            if (document.getId().equals(food_ID)) {
                                ArrayList<String> key_array = new ArrayList<String>(document.getData().keySet());

                                final Dialog dialog = new Dialog(HistoryActivity.this);
                                dialog.setContentView(R.layout.dialog_custom);
                                dialog.setTitle("Information...");

                                LinearLayout linear_body = dialog.findViewById(R.id.linear_body);

                                ImageView img_info_food = dialog.findViewById(R.id.img_info_food);
                                ImageView img_info_food_remain = dialog.findViewById(R.id.img_info_food_remain);

                                TextView txt_info_foodname = dialog.findViewById(R.id.txt_info_foodname);
                                TextView txt_info_foodgroup = dialog.findViewById(R.id.txt_info_foodgroup);
                                TextView txt_info_statue = dialog.findViewById(R.id.txt_info_statue);
                                TextView txt_info_calorie = dialog.findViewById(R.id.txt_info_calorie);
                                TextView txt_info_calorie_remain = dialog.findViewById(R.id.txt_info_calorie_remain);
                                TextView txt_info_calorie_real = dialog.findViewById(R.id.txt_info_calorie_real);

                                for (int i = 0; i < key_array.size(); i++) {
                                    if (key_array.get(i).equals("imgurl")) {
                                        Picasso.with(HistoryActivity.this).load(document.getData().get("imgurl").toString()).into(img_info_food);
                                    } else if(key_array.get(i).equals("imgurl_remain")) {
                                        if (document.getData().get("imgurl_remain").toString() != "") {
                                            Picasso.with(HistoryActivity.this).load(document.getData().get("imgurl_remain").toString()).into(img_info_food_remain);
                                        }
                                    } else if (key_array.get(i).equals("foodname")) {
                                        txt_info_foodname.setText(document.getData().get("foodname").toString());
                                    } else if (key_array.get(i).equals("foodgroup")) {
                                        txt_info_foodgroup.setText(document.getData().get("foodgroup").toString());
                                    } else if (key_array.get(i).equals("time")) {
                                        txt_info_statue.setText(document.getData().get("time").toString());
                                    } else if (key_array.get(i).equals("calories")) {
                                        calorie_plan = Double.valueOf(document.getData().get("calories").toString()) * Double.valueOf(document.getData().get("servingWeight").toString());
                                        txt_info_calorie.setText(String.format("%.1f", Double.valueOf(document.getData().get("calories").toString()) * Double.valueOf(document.getData().get("servingWeight").toString())) + " Cal");
                                    } else if (key_array.get(i).equals("calories_remain")) {
                                        txt_info_calorie_remain.setText(String.format("%.1f", Double.valueOf(document.getData().get("calories_remain").toString())) + " Cal");
                                    } else {
                                        LinearLayout linearLayout = new LinearLayout(HistoryActivity.this);
                                        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

                                        TextView textView_key = new TextView(HistoryActivity.this);
                                        textView_key.setTextColor(getResources().getColor(R.color.colorAccent));
                                        TextView textView_value = new TextView(HistoryActivity.this);

                                        textView_key.setText(key_array.get(i) + " : ");
                                        textView_value.setText(document.getData().get(key_array.get(i)).toString());

                                        linearLayout.addView(textView_key);
                                        linearLayout.addView(textView_value);

                                        linear_body.addView(linearLayout);
                                    }
                                    txt_info_calorie_real.setText(String.format("%.1f", (
                                            Double.valueOf(document.getData().get("calories").toString()) * Double.valueOf(document.getData().get("servingWeight").toString())
                                                    - Double.valueOf(document.getData().get("calories_remain").toString()))) + " Cal");
                                }
                                dialog.show();
                                loadingDialog.hide();
                            }
                        }
                    } else {
                        loadingDialog.hide();
                    }
                }
            });
    }

    public void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e("FoodRecognitionExample", ex.getMessage(), ex);
                // TODO: return and toast
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(HistoryActivity.this, "com.dev.food_colorie_counter.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 10) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result.get(0).equals("please take the photo")
                    || result.get(0).equals("please take a photo")
                    || result.get(0).equals("Please take the photo")
                    || result.get(0).equals("Please take a photo")
                    || result.get(0).equals("please take photo")
                    || result.get(0).equals("please take photo")
                    || result.get(0).equals("Please take photo")
                    || result.get(0).equals("Please take photo")) takePicture();
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap original = setPic();
            Bitmap cropped = ImageUtil.cropCenterImage(original, 544, 544); // crop center image and resize to 544x544
            saveToSD(cropped);
            Fileuploader();
        }
    }

    private void showAlertbox(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please input the remain rate.");

        final EditText input = new EditText(this);

        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                rate = Double.valueOf(input.getText().toString());
                updateData();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private Bitmap setPic() {
        int targetW = 544;
        int targetH = 544;

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

        try {
            bitmap = ImageUtil.rotateImageIfRequired(bitmap, Uri.parse(mCurrentPhotoPath));
        } catch (IOException e) {
            Log.e("FoodRecognitionExample", e.getMessage(),e);
        }

        return bitmap;
    }

    public void saveToSD(Bitmap outputImage){
        if (isStoragePermissionGranted()) {
            File storagePath = new File(Environment.getExternalStorageDirectory() + "/MyPhotos/");
            storagePath.mkdirs();

            DateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String date = df.format(Calendar.getInstance().getTime());

            String filename = "profile_" + date + ".jpg";
            myImage = new File(storagePath, filename);

            try {
                FileOutputStream out = new FileOutputStream(myImage);
                outputImage.compress(Bitmap.CompressFormat.JPEG, 80, out);

                out.flush();
                out.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String getExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    private void Fileuploader() {
        loadingDialog = new LoadingDialog(HistoryActivity.this, false);
        final StorageReference Ref = storageRef.child(imageFileName + "." + getExtension(photoURI));

        Ref.putFile(photoURI).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()){
                    throw task.getException();
                }
                return Ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()){
                    downUri = task.getResult();
                    Log.d(TAG, "onComplete: Url: "+ downUri.toString());

                    showAlertbox();
                }
            }
        });
    }

    public void updateData(){
        db.collection("food_informations/" + Preferences.getValue_String(HistoryActivity.this, Preferences.user_ID) + "/"
                + Preferences.getValue_String(HistoryActivity.this, Preferences.today_date))
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());

                            if (imgurl.equals(document.getData().get("imgurl").toString())) {
                                DocumentReference contact = db.collection("food_informations/" + Preferences.getValue_String(HistoryActivity.this, Preferences.user_ID) + "/"
                                        + Preferences.getValue_String(HistoryActivity.this, Preferences.today_date)).document(document.getId());
                                contact.update("imgurl_remain", downUri.toString());
                                contact.update("calories_remain", calorie_plan * rate)
                                    .addOnSuccessListener(new OnSuccessListener < Void > () {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(HistoryActivity.this, "Updated Successfully",
                                                    Toast.LENGTH_SHORT).show();
                                            loadingDialog.hide();
                                        }
                                    });
                            }

                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                }
            });
    }

    public boolean isStoragePermissionGranted() {
        String TAG = "Storage Permission";
        if (Build.VERSION.SDK_INT >= 23) {
            if (this.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {
                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
        switch (view.getId()) {
            case R.id.btn_detail:
                String food_id = historyList.get(position).getFood_ID();
                getFoodInfo(food_id);
                break;
            case R.id.btn_add:
                imgurl = historyList.get(position).getFoodimage();
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, 10);
                } else {
                    Toast.makeText(this, "Your Device don't Support speech input", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
}
