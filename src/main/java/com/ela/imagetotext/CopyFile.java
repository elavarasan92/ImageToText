package com.ela.imagetotext;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class CopyFile {
	public static void main(String[] args) {
		File infile = new File("C:\\MyInputFile.txt");
		File outfile = new File("C:\\MyOutputFile.txt");
		extracted(infile, outfile);
	}

	public static void copyFileUsingApache(File from, File to) throws IOException {
		FileUtils.copyFile(from, to);
	}

	public static void extracted(File inputFile, File outputFile) {
		FileInputStream instream = null;
		FileOutputStream outstream = null;
		try {

			instream = new FileInputStream(inputFile);
			outstream = new FileOutputStream(outputFile);

			byte[] buffer = new byte[1024];

			int length;
			/*
			 * copying the contents from input stream to output stream using read and write
			 * methods
			 */
			while ((length = instream.read(buffer)) > 0) {
				outstream.write(buffer, 0, length);
			}

			// Closing the input/output file streams
			instream.close();
			outstream.close();

			System.out.println("File copied successfully!!");

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}