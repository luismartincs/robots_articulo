package utils;

public class CameraUtils{
	
	public static int[] getResolution(String cameraName){

		int resolution[] = new int[2];

		if(cameraName.equals("camera1")){
			resolution[0] = 640;
			resolution[1] = 480;
		}else if(cameraName.equals("camera2")){
			resolution[0] = 640;
			resolution[1] = 480;
		}else{
			resolution[0] = 640;
			resolution[1] = 480;
		}

		return resolution;
	}

	
}