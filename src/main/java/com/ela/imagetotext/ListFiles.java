package com.ela.imagetotext;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

public class ListFiles {
	
	public static void listFilesForFolder(final File inputFolder) {
	    for (final File inputFile : inputFolder.listFiles()) {
	        if (inputFile.isDirectory()) {
	            listFilesForFolder(inputFile);
	        } else {
	            System.out.println(inputFile.getName());
	            String fileNameWithOutExt = FilenameUtils.removeExtension(inputFile.getName());
	            final File outputFile = new File("D:/Elavarasan/ArchanaImageToTextTask/OutputFolder/"+fileNameWithOutExt);
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

	
	public static void main(String[] args) {
		final File inputFolder = new File("D:/Elavarasan/ArchanaImageToTextTask/InputFolder");
		
		listFilesForFolder(inputFolder);
	}
	
}
