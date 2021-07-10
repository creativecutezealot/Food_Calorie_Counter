package com.dev.food_colorie_counter.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.food_colorie_counter.R;
import com.dev.food_colorie_counter.apiServices.getCaloriesAPIService;
import com.dev.food_colorie_counter.utils.ImageUtil;
import com.dev.food_colorie_counter.utils.LoadingDialog;
import com.dev.food_colorie_counter.utils.Preferences;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = HomeActivity.class.getName();

    ImageView btn_add_breakfast, btn_add_lunch, btn_add_dinner, btn_add_snack, btn_GoHistory, btn_GoProfile;
    ImageView img_round;
    TextView txt_calorie, txt_cal_count_breakfast, txt_cal_count_lunch, txt_cal_count_dinner, txt_cal_count_snack;
    RelativeLayout layout_breakfast, layout_lunch, layout_dinner, layout_snack;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    String mCurrentPhotoPath = "";
    private static String MY_TOKEN = null;

    StorageReference storageRef = FirebaseStorage.getInstance().getReference("Food_Images");
    Uri photoURI;
    String imageFileName = "";
    File myImage = null;

    Uri downUri;
    String today_date = "";

    private LoadingDialog loadingDialog;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

//        if (new SimpleDateFormat("yyyy-MM-dd").format(new Date()).equals("2019-10-19") || new SimpleDateFormat("yyyy-MM-dd").format(new Date()).equals("2019-10-20") || new SimpleDateFormat("yyyy-MM-dd").format(new Date()).equals("2019-10-21"))
        initUI();

        RunUpdateLoop();

        db = FirebaseFirestore.getInstance();

        today_date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        Preferences.setValue_String(this, Preferences.today_date, today_date);

        txt_calorie.setText(String.valueOf(Preferences.getValue_Int(this, Preferences.groupCalorie)));
        calc_calories();
    }

    private void initUI(){
        btn_add_breakfast = findViewById(R.id.btn_add_breakfast);
        btn_add_lunch = findViewById(R.id.btn_add_lunch);
        btn_add_dinner = findViewById(R.id.btn_add_dinner);
        btn_add_snack = findViewById(R.id.btn_add_snack);
        btn_GoHistory = findViewById(R.id.btn_GoHistory);
        btn_GoProfile = findViewById(R.id.btn_GoProfile);

        txt_calorie = findViewById(R.id.txt_calorie);
        txt_cal_count_breakfast = findViewById(R.id.txt_cal_count_breakfast);
        txt_cal_count_lunch = findViewById(R.id.txt_cal_count_lunch);
        txt_cal_count_dinner = findViewById(R.id.txt_cal_count_dinner);
        txt_cal_count_snack = findViewById(R.id.txt_cal_count_snack);

        layout_breakfast = findViewById(R.id.layout_breakfast);
        layout_lunch = findViewById(R.id.layout_lunch);
        layout_dinner = findViewById(R.id.layout_dinner);
        layout_snack = findViewById(R.id.layout_snack);

        img_round = findViewById(R.id.img_round);
        Drawable background = img_round.getDrawable();
        background.setAlpha(60);

        btn_add_breakfast.setOnClickListener(this);
        btn_add_lunch.setOnClickListener(this);
        btn_add_dinner.setOnClickListener(this);
        btn_add_snack.setOnClickListener(this);
        btn_GoHistory.setOnClickListener(this);
        btn_GoProfile.setOnClickListener(this);

        MY_TOKEN = getString(R.string.caloriemama_token);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
        switch (view.getId()){
            case R.id.btn_add_breakfast:
                Preferences.setValue_String(HomeActivity.this, Preferences.timestatue, "breakfast");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, 10);
                } else {
                    Toast.makeText(this, "Your Device don't Support speech input", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_add_lunch:
                Preferences.setValue_String(HomeActivity.this, Preferences.timestatue, "lunch");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, 10);
                } else {
                    Toast.makeText(this, "Your Device don't Support speech input", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_add_dinner:
                Preferences.setValue_String(HomeActivity.this, Preferences.timestatue, "dinner");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, 10);
                } else {
                    Toast.makeText(this, "Your Device don't Support speech input", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_add_snack:
                Preferences.setValue_String(HomeActivity.this, Preferences.timestatue, "snack");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, 10);
                } else {
                    Toast.makeText(this, "Your Device don't Support speech input", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_GoHistory:
                Intent historyintent = new Intent(HomeActivity.this, HistoryActivity.class);
                startActivity(historyintent);
                break;
            case R.id.btn_GoProfile:
                Intent profileintent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(profileintent);
                break;
            default:
                break;
        }
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

            Bitmap original = setPic(); // set image to the ImageView

            Bitmap cropped = ImageUtil.cropCenterImage(original, 544, 544); // crop center image and resize to 544x544
            saveToSD(cropped);
            Fileuploader();
        }
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
        loadingDialog = new LoadingDialog(HomeActivity.this, false);
        final StorageReference Ref = storageRef.child(imageFileName + "." + getExtension(photoURI));

        Ref.putFile(photoURI).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) throw task.getException();
                return Ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()){
                    downUri = task.getResult();
                    Log.d(TAG, "onComplete: Url: "+ downUri.toString());
                    getCalories(myImage);
                }
            }
        });
    }

    public void calc_calories() {
        db.collection("food_informations/" + Preferences.getValue_String(HomeActivity.this, Preferences.user_ID) + "/"
                + Preferences.getValue_String(HomeActivity.this, Preferences.today_date))
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        Double calc_calories = 0.0;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String time = document.getData().get("time").toString();
                            String calories = document.getData().get("calories").toString();
                            String calories_remain = document.getData().get("calories_remain").toString();
                            String servingWeight = document.getData().get("servingWeight").toString();

                            if (time.equals("breakfast")) {
                                txt_cal_count_breakfast.setText(String.format("%.1f", Double.valueOf(txt_cal_count_breakfast.getText().toString()) + Double.valueOf(calories) * Double.valueOf(servingWeight) - Double.valueOf(calories_remain)));
                            } else if (time.equals("lunch")) {
                                txt_cal_count_lunch.setText(String.format("%.1f", Double.valueOf(txt_cal_count_lunch.getText().toString()) + Double.valueOf(calories) * Double.valueOf(servingWeight) - Double.valueOf(calories_remain)));
                            } else if (time.equals("dinner")) {
                                txt_cal_count_dinner.setText(String.format("%.1f", Double.valueOf(txt_cal_count_dinner.getText().toString()) + Double.valueOf(calories) * Double.valueOf(servingWeight) - Double.valueOf(calories_remain)));
                            } else if (time.equals("snack")) {
                                txt_cal_count_snack.setText(String.format("%.1f", Double.valueOf(txt_cal_count_snack.getText().toString()) + Double.valueOf(calories) * Double.valueOf(servingWeight) - Double.valueOf(calories_remain)));
                            }
                            calc_calories = calc_calories + Double.parseDouble(calories) * Double.valueOf(servingWeight) - Double.parseDouble(calories_remain);
                        }
                        txt_calorie.setText(String.format("%.1f", Double.valueOf(txt_calorie.getText().toString()) - Double.valueOf(calc_calories)));
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                        loadingDialog.hide();
                    }
                }
            });
    }

    public void getCalories(File foodimage){
        new getCaloriesAPIService(
                HomeActivity.this,
                "https://api-2445582032290.production.gw.apicast.io/v1/foodrecognition/full?user_key=" + MY_TOKEN,
                foodimage,
                new getCaloriesAPIService.OnResultReceived() {

                    @Override
                    public void onResult(String result) {
                        if (result.equals("")) return;
                        try {
                            JSONObject json = new JSONObject(result);
                            if (json.getJSONArray("results").length() != 0) {
                                JSONArray results = json.getJSONArray("results");
//                                for (int i = 0; i < results.length(); i++) {
                                for (int i = 0; i < 1; i++) {
                                    JSONObject results_jsonObject = results.getJSONObject(i);
                                    if (results_jsonObject.getJSONArray("items").length() != 0) {
                                        JSONArray items = results_jsonObject.getJSONArray("items");
//                                        for (int j = 0; j < items.length(); j++) {
                                        for (int j = 0; j < 1; j++) {
                                            JSONObject items_jsonObject = items.getJSONObject(j);

                                            String name = items_jsonObject.getString("name");
                                            String group = items_jsonObject.getString("group");

                                            Map<String, Object> data = new HashMap<>();

                                            data.put("foodname", name);
                                            data.put("foodgroup", group);
                                            data.put("imgurl", downUri.toString());
                                            data.put("imgurl_remain", "");
                                            data.put("calories_remain", 0.0);
                                            data.put("date", Preferences.getValue_String(HomeActivity.this, Preferences.today_date));

                                            if (Preferences.getValue_String(HomeActivity.this, Preferences.timestatue).equals("breakfast")) {
                                                data.put("time", "breakfast");
                                            } else if (Preferences.getValue_String(HomeActivity.this, Preferences.timestatue).equals("lunch")) {
                                                data.put("time", "lunch");
                                            } else if (Preferences.getValue_String(HomeActivity.this, Preferences.timestatue).equals("dinner")) {
                                                data.put("time", "dinner");
                                            } else if (Preferences.getValue_String(HomeActivity.this, Preferences.timestatue).equals("snack")){
                                                data.put("time", "snack");
                                            }

                                            JSONObject nutrition_jsonObject = items_jsonObject.getJSONObject("nutrition");

                                            try {
                                                Iterator iteratorObj = nutrition_jsonObject.keys();
                                                while (iteratorObj.hasNext()) {
                                                    String nutrition_key = (String) iteratorObj.next();
                                                    Double nutrition_value = nutrition_jsonObject.getDouble(nutrition_key);

                                                    data.put(nutrition_key, nutrition_value);
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                            if (items_jsonObject.getJSONArray("servingSizes").length() != 0) {
                                                JSONArray servingSizes = items_jsonObject.getJSONArray("servingSizes");
                                                for (int k = 0; k < 1; k++) {
                                                    JSONObject serving_jsonObject = servingSizes.getJSONObject(k);
                                                    Double servingWeight = serving_jsonObject.getDouble("servingWeight");
                                                    data.put("servingWeight", servingWeight);
                                                }
                                            } else if (items_jsonObject.getJSONArray("servingSizes").length() == 0) {
                                                data.put("servingWeight", 1.0);
                                            }

                                            db.collection("food_informations/" + Preferences.getValue_String(HomeActivity.this, Preferences.user_ID) + "/"
                                                    + Preferences.getValue_String(HomeActivity.this, Preferences.today_date))
                                                .add(data)
                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        loadingDialog.hide();
                                                        Intent intent = new Intent(HomeActivity.this, HistoryActivity.class);
                                                        startActivity(intent);
                                                        Toast.makeText(HomeActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        loadingDialog.hide();
                                                        Toast.makeText(HomeActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                        }
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).execute();
    }

    private void RunUpdateLoop() {

        final Handler h = new Handler();
        h.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                int hours = new Time(System.currentTimeMillis()).getHours();

                layout_snack.setScaleX(1.05f);
                layout_snack.setScaleY(1.05f);
                layout_snack.setBackground(getResources().getDrawable(R.drawable.item_radius_selected));

                if (hours >= 8 && hours <= 10) {
                    layout_breakfast.setScaleX(1.05f);
                    layout_breakfast.setScaleY(1.05f);
                    layout_breakfast.setBackground(getResources().getDrawable(R.drawable.item_radius_selected));

                    layout_lunch.setScaleX(1.00f);
                    layout_lunch.setScaleY(1.00f);
                    layout_lunch.setBackground(getResources().getDrawable(R.drawable.item_radius));

                    layout_dinner.setScaleX(1.00f);
                    layout_dinner.setScaleY(1.00f);
                    layout_dinner.setBackground(getResources().getDrawable(R.drawable.item_radius));

                    btn_add_breakfast.setVisibility(View.VISIBLE);
                    btn_add_lunch.setVisibility(View.INVISIBLE);
                    btn_add_dinner.setVisibility(View.INVISIBLE);

                } else if (hours >= 11 && hours <= 13) {
                    layout_breakfast.setScaleX(1.00f);
                    layout_breakfast.setScaleY(1.00f);
                    layout_breakfast.setBackground(getResources().getDrawable(R.drawable.item_radius));

                    layout_lunch.setScaleX(1.05f);
                    layout_lunch.setScaleY(1.05f);
                    layout_lunch.setBackground(getResources().getDrawable(R.drawable.item_radius_selected));

                    layout_dinner.setScaleX(1.00f);
                    layout_dinner.setScaleY(1.00f);
                    layout_dinner.setBackground(getResources().getDrawable(R.drawable.item_radius));

                    btn_add_breakfast.setVisibility(View.INVISIBLE);
                    btn_add_lunch.setVisibility(View.VISIBLE);
                    btn_add_dinner.setVisibility(View.INVISIBLE);

                } else if (hours >= 19 && hours <= 21) {
                    layout_breakfast.setScaleX(1.00f);
                    layout_breakfast.setScaleY(1.00f);
                    layout_breakfast.setBackground(getResources().getDrawable(R.drawable.item_radius));

                    layout_lunch.setScaleX(1.00f);
                    layout_lunch.setScaleY(1.00f);
                    layout_lunch.setBackground(getResources().getDrawable(R.drawable.item_radius));

                    layout_dinner.setScaleX(1.05f);
                    layout_dinner.setScaleY(1.05f);
                    layout_dinner.setBackground(getResources().getDrawable(R.drawable.item_radius_selected));

                    btn_add_breakfast.setVisibility(View.INVISIBLE);
                    btn_add_lunch.setVisibility(View.INVISIBLE);
                    btn_add_dinner.setVisibility(View.VISIBLE);

                } else {
                    layout_breakfast.setScaleX(1.00f);
                    layout_breakfast.setScaleY(1.00f);
                    layout_breakfast.setBackground(getResources().getDrawable(R.drawable.item_radius));

                    layout_lunch.setScaleX(1.00f);
                    layout_lunch.setScaleY(1.00f);
                    layout_lunch.setBackground(getResources().getDrawable(R.drawable.item_radius));

                    layout_dinner.setScaleX(1.00f);
                    layout_dinner.setScaleY(1.00f);
                    layout_dinner.setBackground(getResources().getDrawable(R.drawable.item_radius));

                    btn_add_breakfast.setVisibility(View.INVISIBLE);
                    btn_add_lunch.setVisibility(View.INVISIBLE);
                    btn_add_dinner.setVisibility(View.INVISIBLE);
                }

                h.postDelayed(this, 1000);
            }
        }, 1000);
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
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(HomeActivity.this, "com.dev.food_colorie_counter.fileprovider", photoFile);
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

    private Bitmap setPic() {
        int targetW = img_round.getWidth();
        int targetH = img_round.getHeight();

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

    public boolean isStoragePermissionGranted() {
        String TAG = "Storage Permission";
        if (Build.VERSION.SDK_INT >= 23) {
            if (this.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else {
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }
}
