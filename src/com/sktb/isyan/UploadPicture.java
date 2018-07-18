package com.sktb.isyan;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import android.os.AsyncTask;
import android.widget.Toast;

public class UploadPicture extends AsyncTask<Object, Object, Object> {
    public boolean busy = true;
    public int resultCode;
    public String resultMessage;

    protected void onPostExecute() {
        busy = false;
    }

	@Override
	protected Object doInBackground(Object... args) {
		busy = true;
		
		//Context context = (Context)args[1];
		String fileName = (String)args[0];
		int id = (Integer)args[1];
    	File sourceFile = new File(fileName);
    	long length = sourceFile.length();
    	if(length > 512 * 1048576)
    	{
    		busy = false;
    		resultCode = -4;
    		return null;
    	}

    	if (sourceFile.isFile()) {
            HttpURLConnection conn = null;
            DataOutputStream dos = null;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            int serverResponseCode = 0;
            
            try
            {
                String upLoadServerUri = "http://www.sktb.biz/fma/includes/upload_andro.php?id=" + id;
        		FileInputStream fileInputStream = new FileInputStream(
                        sourceFile);
                URL url = new URL(upLoadServerUri);

                // Open a HTTP connection to the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE",
                        "multipart/form-data");
                conn.setRequestProperty("Content-Type",
                        "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("file1", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"file1\";filename=\""
                        + fileName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math
                            .min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0,
                            bufferSize);

                }

                // send multipart form data necesssary after file
                // data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens
                        + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                resultMessage = conn
                        .getResponseMessage();

                if (serverResponseCode == 200) {
                } else {
                }

                // close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();
                
                resultCode = serverResponseCode;
            } catch (FileNotFoundException e) {
            	resultCode = -3;
            } catch (MalformedURLException e) {
            	resultCode = -2;
            } catch (IOException e) {
            	resultCode = -1;
            }

            busy = false;
    	}

    	return null;
	}
}
