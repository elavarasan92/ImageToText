package com.ela.imagetotext;


import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.ImageAnnotatorSettings;
import com.google.cloud.vision.v1.ImageSource;
import com.google.protobuf.ByteString;
import com.google.cloud.vision.v1.Block;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.Page;
import com.google.cloud.vision.v1.Paragraph;
import com.google.cloud.vision.v1.Symbol;
import com.google.cloud.vision.v1.TextAnnotation;
import com.google.cloud.vision.v1.Word;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

public class ImageDetection {

	public static void main(String[] args)
	{
		try {
			//setEndpoint();
			
			String fileName = "C:/Users/elavarasan/Desktop/JavaMaven/MavenImageToText/text1.png";
			final File inputFolder = new File("D:/Elavarasan/ArchanaImageToTextTask/InputFolder");
			
			listFilesForFolder(inputFolder);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void listFilesForFolder(final File inputFolder) {
	    for (final File inputFile : inputFolder.listFiles()) {
	        if (inputFile.isDirectory()) {
	            listFilesForFolder(inputFile);
	        } else {
	            System.out.println(inputFile.getName());
	            String fileNameWithOutExt = FilenameUtils.removeExtension(inputFile.getName());
	            final File outputFile = new File("D:/Elavarasan/ArchanaImageToTextTask/OutputFolder/"+fileNameWithOutExt+".txt");
	            String outputFilePath = "D:/Elavarasan/ArchanaImageToTextTask/OutputFolder/"+fileNameWithOutExt+".txt";
	            try {
					detectDocumentText(inputFile.getAbsolutePath(),System.out,outputFilePath);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	            try {
					copyFileUsingApache(inputFile,outputFile);
				} catch (IOException e) {
					System.out.println("Exception in copying : "+e);
					e.printStackTrace();
				}
	        }
	    }
	}

	public static void copyFileUsingApache(File from, File to) throws IOException {
		FileUtils.copyFile(from, to);
	}
	
	public static void detectDocumentText(String filePath, PrintStream out,String outputFilePath)
		    throws Exception, IOException {
		  List<AnnotateImageRequest> requests = new ArrayList<>();

		  ByteString imgBytes = ByteString.readFrom(new FileInputStream(filePath));

		  Image img = Image.newBuilder().setContent(imgBytes).build();
		  Feature feat = Feature.newBuilder().setType(Type.DOCUMENT_TEXT_DETECTION).build();
		  AnnotateImageRequest request =
		      AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
		  requests.add(request);

		  try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
		    BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
		    List<AnnotateImageResponse> responses = response.getResponsesList();
		    client.close();

		    for (AnnotateImageResponse res : responses) {
		      if (res.hasError()) {
		        out.printf("Error: %s\n", res.getError().getMessage());
		        return;
		      }

		      // For full list of available annotations, see http://g.co/cloud/vision/docs
		      TextAnnotation annotation = res.getFullTextAnnotation();
		      for (Page page : annotation.getPagesList()) {
		        String pageText = "";
		        for (Block block : page.getBlocksList()) {
		          String blockText = "";
		          for (Paragraph para : block.getParagraphsList()) {
		            String paraText = "";
		            for (Word word : para.getWordsList()) {
		              String wordText = "";
		              for (Symbol symbol : word.getSymbolsList()) {
		                wordText = wordText + symbol.getText();
		                out.format(
		                    "Symbol text: %s (confidence: %f)\n",
		                    symbol.getText(), symbol.getConfidence());
		              }
		              out.format("Word text: %s (confidence: %f)\n\n", wordText, word.getConfidence());
		              paraText = String.format("%s %s", paraText, wordText);
		            }
		            // Output Example using Paragraph:
		            out.println("\nParagraph: \n" + paraText);
		            out.format("Paragraph Confidence: %f\n", para.getConfidence());
		            blockText = blockText + paraText;
		          }
		          pageText = pageText + blockText;
		        }
		      }
		      out.println("\nComplete annotation:");
		      out.println(annotation.getText());
		      String fileContent = annotation.getText();
		      
		      Path path = Paths.get(outputFilePath);
		    
		      Files.write(path, fileContent.getBytes());
		    }
		  }
		}
  // Change your endpoint
  static void setEndpoint() throws IOException {
    // [START vision_set_endpoint]
    ImageAnnotatorSettings settings =
        ImageAnnotatorSettings.newBuilder().setEndpoint("eu-vision.googleapis.com:443").build();

    // Initialize client that will be used to send requests. This client only needs to be created
    // once, and can be reused for multiple requests. After completing all of your requests, call
    // the "close" method on the client to safely clean up any remaining background resources.
    ImageAnnotatorClient client = ImageAnnotatorClient.create(settings);
    // [END vision_set_endpoint]

    ImageSource imgSource =
        ImageSource.newBuilder()
            .setGcsImageUri("gs://cloud-samples-data/vision/text/screen.jpg")
            .build();
    Image image = Image.newBuilder().setSource(imgSource).build();
    Feature feature = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
    AnnotateImageRequest request =
        AnnotateImageRequest.newBuilder().addFeatures(feature).setImage(image).build();
    List<AnnotateImageRequest> requests = new ArrayList<>();
    requests.add(request);

    BatchAnnotateImagesResponse batchResponse = client.batchAnnotateImages(requests);

    for (AnnotateImageResponse response : batchResponse.getResponsesList()) {
      for (EntityAnnotation annotation : response.getTextAnnotationsList()) {
        System.out.printf("Text: %s\n", annotation.getDescription());
        System.out.println("Position:");
        System.out.printf("%s\n", annotation.getBoundingPoly());
      }
    }
    client.close();
  }
}