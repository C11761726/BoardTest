package com.example.boardtest;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public abstract class AExecuteAsRoot {
    public static boolean canRunRootCommands() {
        boolean retval = false;
        Process suProcess;

        try {
            suProcess = Runtime.getRuntime().exec("su");

            DataOutputStream os = new DataOutputStream(suProcess.getOutputStream());
            DataInputStream osRes = new DataInputStream(suProcess.getInputStream());

            if (null != os && null != osRes) {
                // Getting the id of the current user to check if this is root
                os.writeBytes("id\n");
                os.flush();

                String currUid = osRes.readLine();
                boolean exitSu = false;
                if (null == currUid) {
                    retval = false;
                    exitSu = false;
                    Log.d("ROOT", "Can't get root access or denied by user");
                } else if (true == currUid.contains("uid=0")) {
                    retval = true;
                    exitSu = true;
                    Log.d("ROOT", "Root access granted");
                } else {
                    retval = false;
                    exitSu = true;
                    Log.d("ROOT", "Root access rejected: " + currUid);
                }

                if (exitSu) {
                    os.writeBytes("exit\n");
                    os.flush();
                }
            }
        } catch (Exception e) {
            // Can't get root !
            // Probably broken pipe exception on trying to write to output
            // stream after su failed, meaning that the device is not rooted

            retval = false;
            Log.d("ROOT",
                    "Root access rejected [" + e.getClass().getName() + "] : " + e.getMessage());
        }

        return retval;
    }

    public final String execute() {
        boolean retval = false;
        Runtime runtime = Runtime.getRuntime();
        Process process = null;
        StringBuffer output = new StringBuffer();

        try {
            ArrayList<String> commands = getCommandsToExecute();
            if (null != commands && commands.size() > 0) {
                process = runtime.exec("su");

                DataOutputStream os = new DataOutputStream(process.getOutputStream());

                for (String currCommand : commands) {
                    os.writeBytes(currCommand + "\n");
                    os.flush();
                }

                os.writeBytes("exit\n");
                os.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        process.getInputStream()));
                int read;
                char[] buffer = new char[4096];
                String line = null;
                while ((line = reader.readLine()) != null) {
                    output.append(line);
                }
//                while ((read = reader.read(buffer)) > 0) {
//                    output.append(buffer, 0, read);
//                }
                reader.close();

                try {
                    int suProcessRetval = process.waitFor();
                    if (255 != suProcessRetval) {
                        retval = true;
                    } else {
                        retval = false;
                    }
                    System.out.println("BBBB: "+output.toString()) ;
                } catch (Exception ex) {
                    //Log.e("Error executing root action", ex);
                }
            }
        } catch (IOException ex) {
            Log.w("ROOT", "Can't get root access", ex);
        } catch (SecurityException ex) {
            Log.w("ROOT", "Can't get root access", ex);
        } catch (Exception ex) {
            Log.w("ROOT", "Error executing internal operation", ex);
        }  finally {
            //在结束的时候应该对资源进行回收
            if (process != null) {
                process.destroy();
            }
            runtime.gc();
        }

        return output.toString();
    }

    protected abstract ArrayList<String> getCommandsToExecute();
}
