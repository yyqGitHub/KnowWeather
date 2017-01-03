package com.example.maxenia.knowweather.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by http://blog.csdn.net/wangqilin8888/article/details/7904372
 */
public class GetImageFromNet {

    public static Bitmap getBitmapFromUrl(String imgUrl)
    {
        URL url;
        Bitmap bitmap = null;
        try {
            url = new URL(imgUrl);
            InputStream is = url.openConnection().getInputStream();
         //   BufferedInputStream bis = new BufferedInputStream(is);
            // bitmap = BitmapFactory.decodeStream(bis); 注释1
            byte[] b = getBytes(is);
            bitmap = BitmapFactory.decodeByteArray(b,0,b.length);
        //    bis.close();
        }catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 将InputStream对象转换为Byte[]
     * @param is
     * @return
     * @throws IOException */
    public static byte[] getBytes(InputStream is) throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] b = new  byte[1024];
        int len ;
        while ((len = is.read(b, 0, 1024)) != -1)
        {
            baos.write(b, 0, len);
            baos.flush();
        }
        byte[] bytes = baos.toByteArray();
        return bytes;
    }
}
