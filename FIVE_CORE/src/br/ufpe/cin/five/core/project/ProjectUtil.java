/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved. Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.project;

import br.ufpe.cin.five.core.data.DriverType;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * This class provides a set of useful methods for project process.
 */
public class ProjectUtil {

    public static boolean createFolder(String path) {
        boolean folderCreated = false;
        File folder = new File(path);
        if (!folder.exists()) {
            folderCreated = folder.mkdirs();
        }
        return folderCreated;
    }

    public static void copyFile(String source, String dest) throws IOException {
        File modelDestination = new File(dest);
        File modelSource = new File(source);
        if (modelSource.exists()) {
            FileChannel sourceChannel;
            FileChannel destinationChannel;
            sourceChannel = new FileInputStream(modelSource).getChannel();
            destinationChannel = new FileOutputStream(modelDestination).getChannel();
            sourceChannel.transferTo(0, sourceChannel.size(), destinationChannel);
            sourceChannel.close();
            destinationChannel.close();
        }
    }

    public static void copyDirectory(String src, String dst) throws IOException {
        File srcDir = new File(src);
        File dstDir = new File(dst);
        if (srcDir.isDirectory()) {
            if (!dstDir.exists()) {
                dstDir.mkdir();
            }
            File[] children = srcDir.listFiles();
            for (int i = 0; i < children.length; i++) {
                if (!children[i].isHidden()) {
                    copyDirectory(children[i].getPath(), dst + File.separator + children[i].getName());
                }
            }
        } else { // This method is implemented in Copying a File
            copyFile(srcDir.getPath(), dstDir.getPath());
        }
    }

    public static void deleteDirectory(File file, boolean deleteFolders) throws SecurityException {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            for (int i = 0; i < children.length; i++) {
                deleteDirectory(children[i], deleteFolders);
            }
            if (deleteFolders) {
                file.delete();
            }
        } else {
            file.delete();
        }
    }

    public static void deleteDirectory(File file, boolean deleteFolders, String extensions) throws SecurityException {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            for (int i = 0; i < children.length; i++) {
                deleteDirectory(children[i], deleteFolders,extensions);
            }
            if (deleteFolders) {
                file.delete();
            }
        } else {
            if (file.getPath().endsWith(extensions)){
                file.delete();
            }
        }
    }
    
    public static void checkExists(File... dirs) {
        for (File dir : dirs) {
            if (!dir.exists()) {
                dir.mkdirs();
            }
            else {
                emptyDir(dir);
            }
        }
    }  
    
    public static void emptyDir(File dir) {
        for (File file : dir.listFiles()) {
            if (dir.isDirectory()) {
                deleteDir(file);
            } else {
                file.delete();
            }
        }
    }  
    
    private static Boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                boolean success = deleteDir(file);
                if (!success) {
                    return false;
                }
            }
        } // The directory is now empty so delete it
        return dir.delete();
    }    
    
    public static ProjectType getProjectType(String projectType) {
        if(projectType.equals("ASR")) return ProjectType.ASR;
        if(projectType.equals("ASV")) return ProjectType.ASV;
        if(projectType.equals("TTS")) return ProjectType.TTS;
        else  return null;            
    }    
}
