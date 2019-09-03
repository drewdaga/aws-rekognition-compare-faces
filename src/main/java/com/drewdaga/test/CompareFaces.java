/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.drewdaga.test;

import software.amazon.awssdk.core.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.BoundingBox;
import software.amazon.awssdk.services.rekognition.model.CompareFacesMatch;
import software.amazon.awssdk.services.rekognition.model.CompareFacesRequest;
import software.amazon.awssdk.services.rekognition.model.CompareFacesResponse;
import software.amazon.awssdk.services.rekognition.model.ComparedFace;
import software.amazon.awssdk.services.rekognition.model.Image;
import software.amazon.awssdk.utils.IoUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;

public class CompareFaces {

  public static void main(String[] args) throws Exception{
    Float similarityThreshold = 70F;
    //Replace sourceFile and targetFile with the image files you want to compare.
    String sourceImage = "source.jpg";
    String targetImage = "target.jpg";
    ByteBuffer sourceImageBytes=null;
    ByteBuffer targetImageBytes=null;

    RekognitionClient rekognitionClient = RekognitionClient.builder()
            .region(Region.US_EAST_2)
            .build();

    //Load source and target images and create input parameters
    try (InputStream inputStream = new FileInputStream(new File(sourceImage))) {
      sourceImageBytes = ByteBuffer.wrap(IoUtils.toByteArray(inputStream));
    }
    catch(Exception e)
    {
      System.out.println("Failed to load source image " + sourceImage);
      System.exit(1);
    }
    try (InputStream inputStream = new FileInputStream(new File(targetImage))) {
      targetImageBytes = ByteBuffer.wrap(IoUtils.toByteArray(inputStream));
    }
    catch(Exception e)
    {
      System.out.println("Failed to load target images: " + targetImage);
      System.exit(1);
    }

    Image source= Image.builder().bytes(sourceImageBytes).build();

    Image target=Image.builder().bytes(targetImageBytes).build();

    CompareFacesRequest request = CompareFacesRequest.builder()
            .sourceImage(source)
            .targetImage(target)
            .similarityThreshold(similarityThreshold)
            .build();

    // Call operation
    CompareFacesResponse compareFacesResult=rekognitionClient.compareFaces(request);


    // Display results
    List<CompareFacesMatch> faceDetails = compareFacesResult.faceMatches();
    for (CompareFacesMatch match: faceDetails){
      ComparedFace face= match.face();
      BoundingBox position = face.boundingBox();
      System.out.println("Face at " + position.left().toString()
              + " " + position.top()
              + " matches with " + face.confidence().toString()
              + "% confidence.");

    }
    List<ComparedFace> uncompared = compareFacesResult.unmatchedFaces();

    System.out.println("There was " + uncompared.size()
            + " face(s) that did not match");
    System.out.println("Source image rotation: " + compareFacesResult.sourceImageOrientationCorrection());
    System.out.println("target image rotation: " + compareFacesResult.targetImageOrientationCorrection());
  }
}