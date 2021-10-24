package com.project.iway.Util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by joejet on 10/3/2017.
 */

public class ImageHelper {

    private Context _context;
    public Bitmap icon;
    public int drawable;

    private final String TAG = getClass().getSimpleName();
    private static final String SCHEME_FILE = "file";
    private static final String SCHEME_CONTENT = "content";

    public ImageHelper(Context context){
        this._context = context;
        icon = BitmapFactory.decodeResource(context.getResources(), drawable);
    }

    public int convertDipToPixels(float dips){
        Resources r = _context.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dips, r.getDisplayMetrics());
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;

        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    public Bitmap decodeBitmapFromPath(String filePath){
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        options.inSampleSize = calculateInSampleSize(options, convertDipToPixels(150), convertDipToPixels(200));
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inJustDecodeBounds = false;

        scaledBitmap = BitmapFactory.decodeFile(filePath, options);
        return scaledBitmap;
    }


    public static String encodeImageToBase64(Bitmap bm)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,80,baos);
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

    }

    public static String encodeSignatureToBase64(Bitmap bm)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG,100,baos);
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

    }

    public Bitmap Base64ToImage(String encodedImage){

        byte[] decodedString = Base64.decode(encodedImage.getBytes(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        return  decodedByte;
    }

    public Bitmap SclaedBase64ToImage(String encodedImage){

        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        options.inSampleSize = calculateInSampleSize(options, convertDipToPixels(150), convertDipToPixels(200));
        //options.inDither = false;
        //options.inPurgeable = true;
        //options.inInputShareable = true;
        options.inJustDecodeBounds = false;

        byte[] decodedString = Base64.decode(encodedImage.getBytes(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length,options);

        return  decodedByte;
    }

    public static Bitmap thumbNail(String encodedImage, int width, int height){
        byte[] decodedString = Base64.decode(encodedImage.getBytes(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return ThumbnailUtils.extractThumbnail(decodedByte,width,height);
    }

    private static String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public Bitmap decodeUriToBitmap(Uri IMAGE_URI) {

        InputStream image_stream = null;
        try {
            image_stream = _context.getContentResolver().openInputStream(IMAGE_URI);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap bitmap= BitmapFactory.decodeStream(image_stream );
        return bitmap;
    }



    public static Bitmap decodeFileToBitmap(File f) {
        Bitmap b = null;

        //Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
            BitmapFactory.decodeStream(fis, null, o);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int IMAGE_MAX_SIZE = 1024;
        int scale = 1;
        if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
            scale = (int) Math.pow(2, (int) Math.ceil(Math.log(IMAGE_MAX_SIZE /
                    (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
        }

        //Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        try {
            fis = new FileInputStream(f);
            b = BitmapFactory.decodeStream(fis, null, o2);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

       // Log.d(TAG, "Width :" + b.getWidth() + " Height :" + b.getHeight());

       // destFile = new File(file, "img_" + dateFormatter.format(new Date()).toString() + ".png");

        try {
            FileOutputStream out = new FileOutputStream(String.valueOf(fis));
            b.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }


    public String getPathFromUri(Uri uriPhoto) {
        if (uriPhoto == null)
            return null;

        if (SCHEME_FILE.equals(uriPhoto.getScheme())) {
            return uriPhoto.getPath();
        } else if (SCHEME_CONTENT.equals(uriPhoto.getScheme())) {
            final String[] filePathColumn = {MediaStore.MediaColumns.DATA,
                    MediaStore.MediaColumns.DISPLAY_NAME};
            Cursor cursor = null;
            try {
                cursor = _context.getContentResolver().query(uriPhoto, filePathColumn, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    final int columnIndex = (uriPhoto.toString()
                            .startsWith("PACSCamera")) ? cursor
                            .getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
                            : cursor.getColumnIndex(MediaStore.MediaColumns.DATA);

                    // Picasa images on API 13+
                    if (columnIndex != -1) {
                        String filePath = cursor.getString(columnIndex);
                        if (!TextUtils.isEmpty(filePath)) {
                            return filePath;
                        }
                    }
                }
            } catch (IllegalArgumentException e) {
                // Nothing we can do
                Log.d(TAG, "IllegalArgumentException");
                e.printStackTrace();
            } catch (SecurityException ignored) {
                Log.d(TAG, "SecurityException");
                // Nothing we can do
                ignored.printStackTrace();
            } finally {
                if (cursor != null)
                    cursor.close();
            }
        }
        return null;
    }


    public static String getPathFromPhotosUri(Activity activity,Uri uriPhoto) {
        if (uriPhoto == null)
            return null;

        FileInputStream input = null;
        FileOutputStream output = null;
        try {
            ParcelFileDescriptor pfd = activity.getContentResolver().openFileDescriptor(uriPhoto, "r");
            FileDescriptor fd = pfd.getFileDescriptor();
            input = new FileInputStream(fd);

            String tempFilename = getTempFilename(activity);
            output = new FileOutputStream(tempFilename);

            int read;
            int bite = 4096 * 2;
            byte[] bytes = new byte[bite];
            while ((read = input.read(bytes)) != -1) {
                output.write(bytes, 0, read);
            }
            return tempFilename;
        } catch (IOException ignored) {
            // Nothing we can do
        } finally {
            closeSilently(input);
           // closeSilently(output);
        }
        return null;
    }


    public static void closeSilently(FileInputStream c) {
        if (c == null)
            return;
        try {
            c.close();
        } catch (Throwable t) {
            // Do nothing
        }
    }

    private static String getTempFilename(Context context) throws IOException {
        File outputDir = context.getCacheDir();
        File outputFile = File.createTempFile("image", "tmp", outputDir);
        return outputFile.getAbsolutePath();
    }

    private void copyFile(File sourceFile, File destFile) throws IOException {
        if (!sourceFile.exists()) {
            return;
        }
    }

    public static Bitmap getBitmap(Activity activity, Uri imageToUploadUri, int sizeDivisor) {
        try {
            int orientation = new ExifInterface(imageToUploadUri.getPath()).getAttributeInt("Orientation", 1);
            int angle = 0;
            if (orientation == 6) {
                angle = 90;
            } else if (orientation == 3) {
                angle = 180;
            } else if (orientation == 8) {
                angle = 270;
            }
            Matrix mat = new Matrix();
            mat.postRotate((float) angle);
            Bitmap bmp = BitmapFactory.decodeStream(new FileInputStream(imageToUploadUri.getPath()), null, null);
            Bitmap correctBmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), mat, true);
            return scaleBitmaptoBitmap(correctBmp, correctBmp.getWidth() / sizeDivisor, correctBmp.getHeight() / sizeDivisor);
        } catch (IOException e) {
            try {
                Toast.makeText(activity, "" + e.toString(), Toast.LENGTH_LONG).show();
                Log.w("TAG", "-- Error in setting image");
                return null;
            } catch (Exception e2) {
                Toast.makeText(activity, "" + e2.getMessage(), Toast.LENGTH_LONG).show();
                return null;
            }
        } catch (OutOfMemoryError oom) {
            Toast.makeText(activity, "" + oom.toString(), Toast.LENGTH_LONG).show();
            Log.w("TAG", "-- OOM Error in setting image");
            return null;
        }
    }

    public static Bitmap scaleBitmaptoBitmap(Bitmap bitmap, int wantedWidth, int wantedHeight) {
        Bitmap output = Bitmap.createBitmap(wantedWidth, wantedHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Matrix m = new Matrix();
        m.setScale(((float) wantedWidth) / ((float) bitmap.getWidth()), ((float) wantedHeight) / ((float) bitmap.getHeight()));
        canvas.drawBitmap(bitmap, m, new Paint());
        return output;
    }

    public static String getEncoded64ImageStringFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return Base64.encodeToString(stream.toByteArray(),Base64.DEFAULT);
    }

    public static byte[] getBytesFromBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
    }

    public static Bitmap resizeBitmap(Activity activity, String path, int maxSize) {
        Uri uri = Uri.fromFile(new File(path));
        int IMAGE_MAX_SIZE = maxSize;
        try {
            Bitmap b;
            InputStream in = activity.getContentResolver().openInputStream(uri);
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, o);
            in.close();
            int scale = 1;
            while (((double) (o.outWidth * o.outHeight)) * (1.0d / Math.pow((double) scale, 2.0d)) > ((double) IMAGE_MAX_SIZE)) {
                scale++;
            }
            Log.d("", "scale = " + scale + ", orig-width: " + o.outWidth + ", orig-height: " + o.outHeight);
            in = activity.getContentResolver().openInputStream(uri);
            if (scale > 1) {
                scale--;
                o = new BitmapFactory.Options();
                o.inSampleSize = scale;
                b = BitmapFactory.decodeStream(in, null, o);
                int height = b.getHeight();
                int width = b.getWidth();
                Log.d("", "1th scale operation dimenions - width: " + width + ", height: " + height);
                double y = Math.sqrt(((double) IMAGE_MAX_SIZE) / (((double) width) / ((double) height)));
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) ((y / ((double) height)) * ((double) width)), (int) y, true);
                b.recycle();
                b = scaledBitmap;
                System.gc();
            } else {
                b = BitmapFactory.decodeStream(in);
            }
            in.close();
            Log.d("", "bitmap size - width: " + b.getWidth() + ", height: " + b.getHeight());
            return b;
        } catch (IOException e) {
            Log.e("", e.getMessage(), e);
            return null;
        }
    }

    public static Bitmap getCroppedBitmap(Bitmap bitmap) {
        try {
            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                    bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);

            final int color = 0xff424242;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                    bitmap.getWidth() / 2, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);
            //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
            //return _bmp;
            return output;
        } catch (Exception e){
            e.printStackTrace();
            return bitmap;
        }
    }
}
