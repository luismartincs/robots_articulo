/*
#include <iostream>
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/imgproc/imgproc.hpp"

using namespace cv;
using namespace std;

 int main( int argc, char** argv )
 {
    VideoCapture vcap; //capture the video from web cam

	const std::string videoStreamAddress = "http://192.168.2.104:8080/videofeed?dummy=param.mjpg";

		//open the video stream and make sure it's opened
		std::cout << "Conectando..." << std::endl;
		if(!vcap.open(videoStreamAddress)) {
			std::cout << "Error opening video stream or file" << std::endl;
			return -1;
		}
		std::cout << "¡Conexion exitosa!" << std::endl;


    namedWindow("Control", CV_WINDOW_AUTOSIZE); //create a window called "Control"

  int iLowH = 0;
 int iHighH = 179;

  int iLowS = 0; 
 int iHighS = 255;

  int iLowV = 0;
 int iHighV = 255;

  //Create trackbars in "Control" window
 cvCreateTrackbar("LowB", "Control", &iLowH, 255); //Hue (0 - 179)
 cvCreateTrackbar("HighB", "Control", &iHighH, 255);

  cvCreateTrackbar("LowG", "Control", &iLowS, 255); //Saturation (0 - 255)
 cvCreateTrackbar("HighG", "Control", &iHighS, 255);

  cvCreateTrackbar("LowR", "Control", &iLowV, 255); //Value (0 - 255)
 cvCreateTrackbar("HighR", "Control", &iHighV, 255);

    while (true)
    {
        Mat imgOriginal;

        bool bSuccess = vcap.read(imgOriginal); // read a new frame from video

         if (!bSuccess) //if not success, break loop
        {
             cout << "Cannot read a frame from video stream" << endl;
             break;
        }

    Mat imgHSV;

   cvtColor(imgOriginal, imgHSV, COLOR_BGR2HSV); //Convert the captured frame from BGR to HSV
 
  Mat imgThresholded;

   inRange(imgOriginal, Scalar(iLowH, iLowS, iLowV), Scalar(iHighH, iHighS, iHighV), imgThresholded); 
 
  //morphological opening (remove small objects from the foreground)
  erode(imgThresholded, imgThresholded, getStructuringElement(MORPH_ELLIPSE, Size(5, 5)) );
  dilate( imgThresholded, imgThresholded, getStructuringElement(MORPH_ELLIPSE, Size(5, 5)) ); 

   //morphological closing (fill small holes in the foreground)
  dilate( imgThresholded, imgThresholded, getStructuringElement(MORPH_ELLIPSE, Size(5, 5)) ); 
  erode(imgThresholded, imgThresholded, getStructuringElement(MORPH_ELLIPSE, Size(5, 5)) );

   imshow("Thresholded Image", imgThresholded); //show the thresholded image
  imshow("Original", imgOriginal); //show the original image

        if (waitKey(30) == 27) //wait for 'esc' key press for 30ms. If 'esc' key is pressed, break loop
       {
            cout << "esc key is pressed by user" << endl;
            break; 
       }
    }

   return 0;

}
*/


#include <iostream>
#include <fstream>
#include <opencv2/core/core.hpp>
#include <opencv2/highgui/highgui.hpp>
#include "cameraparams.h"
#include "patterndetector.h"

using namespace std;
using namespace cv;
using namespace ARma;

#define PAT_SIZE 64//equal to pattern_size variable (see below)
#define SAVE_VIDEO 0 //if true, it saves the video in "output.avi"
#define NUM_OF_PATTERNS 14// define the number of patterns you want to use
#define isIPCam true
#define RES_WIDTH 1600 //1280
#define RES_HEIGHT 1200 //768
#define ROWS 10
#define COLS 15

//1920*1080

string filename1="../patterns/pattern1.png";//id=1
string filename2="../patterns/pattern2.png";//id=1
string filename3="../patterns/pattern3.png";//id=1
string filename4="../patterns/pattern4.png";//id=1
string filename5="../patterns/pattern5.png";//id=2
string filename6="../patterns/pattern6.png";//id=3
string filename7="../patterns/pattern7.png";//id=1
string filename8="../patterns/pattern8.png";//id=1
string filename9="../patterns/pattern9.png";//id=1
string filename10="../patterns/pattern10.png";//id=1
string filename11="../patterns/pattern11.png";//id=3
string filename12="../patterns/pattern12.png";//id=1
string filename13="../patterns/pattern13.png";//id=1
string filename14="../patterns/pattern14.png";//id=1

struct MapCell{
    int ix;
    int iy;
    float x;
    float y;
    float width;
    float height;
};

unsigned int pixelSizeW = RES_WIDTH/COLS;
unsigned int pixelSizeH = RES_HEIGHT/ROWS;

long double prevTime = time(0) * 1000;


static int loadPattern(const string , std::vector<cv::Mat>& , int& );

int main(int argc, char** argv){

    std::ofstream file;

	std::vector<cv::Mat> patternLibrary;
	std::vector<Pattern> detectedPattern;

	//Inicializar celdas del mapa

	std::vector<MapCell> mapCells;
	CvScalar color = cvScalar(255,255,255);

	for(int i=0; i < ROWS;i++){
                for (int j=0; j < COLS; j++) {
                    MapCell mCell;
                    mCell.ix = j;
                    mCell.iy = i;
                    mCell.x = j*pixelSizeW;
                    mCell.y = i*pixelSizeH;
                    mCell.width = pixelSizeW;
                    mCell.height = pixelSizeH;
                    mapCells.push_back(mCell);
        }
    }

    long double prevTime = time(0) * 1000;

	int patternCount=0;

	
	//create patterns' library using rotated versions of patterns 


	loadPattern(filename1, patternLibrary, patternCount);
	loadPattern(filename2, patternLibrary, patternCount);
	loadPattern(filename3, patternLibrary, patternCount);
	loadPattern(filename4, patternLibrary, patternCount);
	loadPattern(filename5, patternLibrary, patternCount);
	loadPattern(filename6, patternLibrary, patternCount);
	loadPattern(filename7, patternLibrary, patternCount);
	loadPattern(filename8, patternLibrary, patternCount);
	loadPattern(filename9, patternLibrary, patternCount);
	loadPattern(filename10, patternLibrary, patternCount);
	loadPattern(filename11, patternLibrary, patternCount);
	loadPattern(filename12, patternLibrary, patternCount);
	loadPattern(filename13, patternLibrary, patternCount);
	loadPattern(filename14, patternLibrary, patternCount);
	cout << patternCount << " patterns are loaded." << endl;
	

	int norm_pattern_size = PAT_SIZE;
	double fixed_thresh = 40;
	double adapt_thresh = 5;//non-used with FIXED_THRESHOLD mode
	int adapt_block_size = 45;//non-used with FIXED_THRESHOLD mode
	double confidenceThreshold = 0.35;
	int mode = 1;//1:FIXED_THRESHOLD, 2: ADAPTIVE_THRESHOLD

	PatternDetector myDetector( fixed_thresh, adapt_thresh, adapt_block_size, confidenceThreshold, norm_pattern_size, mode);

	CvCapture* capture; //= cvCaptureFromCAM(0);

	cv::VideoCapture vcap;

//Se define la IP de la camara remota
	if(isIPCam) {

		const std::string videoStreamAddress = "http://192.168.2.104:8080/videofeed?dummy=param.mjpg";

		//open the video stream and make sure it's opened
		std::cout << "Conectando..." << std::endl;
		if(!vcap.open(videoStreamAddress)) {
			std::cout << "Error opening video stream or file" << std::endl;
			return -1;
		}
		std::cout << "¡Conexion exitosa!" << std::endl;
	} else {

		capture = cvCaptureFromCAM(0);
		
		
		//cvSetCaptureProperty(capture,CV_CAP_PROP_FOURCC,CV_FOURCC('M','J','P','G'));
		//cvSetCaptureProperty(capture,CV_CAP_PROP_FRAME_WIDTH, 1280 );
		//cvSetCaptureProperty(capture,CV_CAP_PROP_FRAME_HEIGHT, 768 );
		
	}

#if (SAVE_VIDEO)
	CvVideoWriter *video_writer = cvCreateVideoWriter( "output.avi", -1, 25, cvSize(640,480) );
#endif

	Mat imgMat;

	while(1){ //modify it for longer/shorter videos
		
				IplImage* img; // = cvQueryFrame(capture);
				Mat imgMat; // = cv::cvarrToMat(img);

				if(isIPCam) {
					if(!vcap.read(imgMat)) {
						std::cout << "No frame" << std::endl;
						cv::waitKey();
					}
				} else {
					img = cvQueryFrame(capture);
					imgMat = cv::cvarrToMat(img);

				}


				//run the detector
 				myDetector.detect(imgMat, cameraMatrix, distortions, patternLibrary, detectedPattern); 


				//Establecer las posiciones de los agentes

				for (unsigned int i =0; i<detectedPattern.size(); i++){
		            Pattern *patternDetected = &detectedPattern.at(i);
		            
		            for (int i=0; i < mapCells.size(); i++) {
		                MapCell mc = mapCells.at(i);
		                
		                if ((patternDetected->center.x > mc.x && patternDetected->center.x < (mc.x + mc.width)) && (patternDetected->center.y > mc.y && patternDetected->center.y < (mc.y + mc.height))) {
		                    patternDetected->ix = mc.ix;
		                    patternDetected->iy = mc.iy;
		                    continue;
		                }
		            }
		            
		        }


				//Dibujar patrones
				for (unsigned int i =0; i<detectedPattern.size(); i++){
					detectedPattern.at(i).draw( imgMat, cameraMatrix, distortions);
				}


				//Dibujar grid e informacion

				for (int i=0; i < mapCells.size(); i++) {
		            MapCell mc = mapCells.at(i);
		            cv::rectangle(imgMat,cvPoint(mc.x,mc.y),cvPoint(mc.x+mc.width,mc.y+mc.height),color);
		        }

				putText(imgMat, "N", Point2f(RES_WIDTH/2,50), FONT_HERSHEY_PLAIN, 1,  Scalar(0,255,0),1);
		        putText(imgMat, "S", Point2f(RES_WIDTH/2,RES_HEIGHT-50), FONT_HERSHEY_PLAIN, 1,  Scalar(0,255,0),1);
		        putText(imgMat, "E", Point2f(RES_WIDTH-50,RES_HEIGHT/2), FONT_HERSHEY_PLAIN, 1,  Scalar(0,255,0),1);
		        putText(imgMat, "O", Point2f(50,RES_HEIGHT/2), FONT_HERSHEY_PLAIN, 1,  Scalar(0,255,0),1);
		        

		        long double currentTime = time(0)*1000;
        
			        if((currentTime - prevTime) >= 5000){
			            prevTime = currentTime;
			            
			            file.open ("../log/agents.txt");

			            for (unsigned int i =0; i<detectedPattern.size(); i++){
			                Pattern *patternDetected = &detectedPattern.at(i);
			                
			                file<<""<< patternDetected->id<<","<<patternDetected->ix<<","<<patternDetected->iy<<","<<patternDetected->center.x<<","<<patternDetected->center.y<<","<<patternDetected->orientationStr<<endl;
			                
			                std::cout<<""<< patternDetected->id<<","<<patternDetected->ix<<","<<patternDetected->iy<<","<<patternDetected->center.x<<","<<patternDetected->center.y<<","<<patternDetected->orientationStr<<endl;
			                
			                file.flush();
		                
		            	}
		        	}
        
           			file.close();


		#if (SAVE_VIDEO)
				cvWriteFrame(video_writer, &((IplImage) imgMat));
		#endif		
				Mat resizedImgMat;
				resize(imgMat,resizedImgMat,Size(800,600));

				//namedWindow("Camera Agent",CV_WINDOW_AUTOSIZE);
				//imshow("Camera Agent", imgMat);
				imshow("Camera Agent",resizedImgMat);
				cvWaitKey(100);

				detectedPattern.clear();

		//}
	
	} // while

#if (SAVE_VIDEO)
	cvReleaseVideoWriter(&video_writer);
#endif
	cvReleaseCapture(&capture);

	return 0;

}

int loadPattern(string filename, std::vector<cv::Mat>& library, int& patternCount){
	Mat img = imread(filename,0);
	
	if(img.cols!=img.rows){

		printf("Not a square pattern");
		return -1;
	}

	int msize = PAT_SIZE; 

	Mat src(msize, msize, CV_8UC1);
	Point2f center((msize-1)/2.0f,(msize-1)/2.0f);
	Mat rot_mat(2,3,CV_32F);
	
	resize(img, src, Size(msize,msize));
	Mat subImg = src(Range(msize/4,3*msize/4), Range(msize/4,3*msize/4));
	library.push_back(subImg);

	rot_mat = getRotationMatrix2D( center, 90, 1.0);

	for (int i=1; i<4; i++){
		Mat dst= Mat(msize, msize, CV_8UC1);
		rot_mat = getRotationMatrix2D( center, -i*90, 1.0);
		warpAffine( src, dst , rot_mat, Size(msize,msize));
		Mat subImg = dst(Range(msize/4,3*msize/4), Range(msize/4,3*msize/4));
		library.push_back(subImg);	
	}

	patternCount++;
	return 1;
}
