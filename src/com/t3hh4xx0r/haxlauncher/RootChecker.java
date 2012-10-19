package com.t3hh4xx0r.haxlauncher;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.t3hh4xx0r.haxlauncher.ExecShell.SHELL_CMD;

	public class RootChecker {

	    public static boolean isDeviceRooted() {
	        if (checkRootMethod1()){return true;}
	        if (checkRootMethod2()){return true;}
	        if (checkRootMethod3()){return true;}
	        return false;
	    }
	
	    public static boolean checkRootMethod1(){
	        String buildTags = android.os.Build.TAGS;
	
	        if (buildTags != null && buildTags.contains("test-keys")) {
	            return true;
	        }
	        return false;
	    }
	
	    public static boolean checkRootMethod2(){
	        try {
	            File file = new File("/system/app/Superuser.apk");
	            if (file.exists()) {
	                return true;
	            }
	        } catch (Exception e) { }
	
	        return false;
	    }
	
	    public static boolean checkRootMethod3() {
	        if (new ExecShell().executeCommand(SHELL_CMD.check_su_binary) != null){
	            return true;
	        }else{
	            return false;
	        }
	    }
	}


	/**
	 * @author Kevin Kowalewski
	 *
	 */
	class ExecShell {
	
	    private static String LOG_TAG = ExecShell.class.getName();
	
	    public static enum SHELL_CMD {
	        check_su_binary(new String[] {"/system/xbin/which","su"}),
	        ;
	
	        String[] command;
	
	        SHELL_CMD(String[] command){
	            this.command = command;
	        }
	    }
	
	    public ArrayList<String> executeCommand(SHELL_CMD shellCmd){
	        String line = null;
	        ArrayList<String> fullResponse = new ArrayList<String>();
	        Process localProcess = null;
	
	        try {
	            localProcess = Runtime.getRuntime().exec(shellCmd.command);
	        } catch (Exception e) {
	            return null;
	        }
	
	        BufferedReader in = new BufferedReader(new InputStreamReader(localProcess.getInputStream()));
	
	        try {
	            while ((line = in.readLine()) != null) {
	                fullResponse.add(line);
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
		
	        return fullResponse;
	    }
}