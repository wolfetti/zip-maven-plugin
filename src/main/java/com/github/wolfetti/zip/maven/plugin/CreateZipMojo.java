/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.wolfetti.zip.maven.plugin;

import java.io.File;
import java.io.IOException;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

/**
 * @author Wolfetti
 */
@Mojo(defaultPhase = LifecyclePhase.PACKAGE, name = "create")
public class CreateZipMojo extends AbstractMojo {
    
    /**
     * The source folder.
     */
    @Parameter(property = "zip.sourceDirectory", defaultValue = "${basedir}/src/main/zip")
    private File sourceDirectory;
    
    /**
     * The target folder.
     */
    @Parameter(property = "zip.outputDirectory", defaultValue = "${basedir}/target/")
    private File outputDirectory;
    
    /**
     * The target folder.
     */
    @Parameter(property = "zip.outputFileName", defaultValue = "${project.artifactId}-${project.version}.zip")
    private String outputFileName;
    
    /**
     * If true the artifact will be attached
     */
    @Parameter(property = "zip.attachArtifact", defaultValue = "true")
    private boolean attachArtifact;
    
    /**
     * If true execution will be skipped
     */
    @Parameter(property = "zip.skipExecution", defaultValue = "false")
    private boolean skipExecution;
    
    /**
     * Maven Project
     */
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;
    
    /**
     * Maven ProjectHelper.
     */
    @Component
    private MavenProjectHelper projectHelper;
    
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        
        if(skipExecution){
            getLog().info("Zip plugin execution skipped.");
            return;
        }
        
        getLog().info("Creating zip file...");
        if(getLog().isDebugEnabled()){
            getLog().debug("Zip source = " + sourceDirectory.getAbsolutePath());
        }
        
        // =====================================================================
        // IN/OUT parameters check
        // =====================================================================
        if(!sourceDirectory.exists()){
            getLog().info("Zip source not exists. Skipping...");
            return;
        }
        
        if(!outputFileName.endsWith(".zip")){
            getLog().debug("Fixing outputFileName: adding '.zip' extension");
            outputFileName += ".zip";
        }
        
        if(!sourceDirectory.isDirectory()){
            throw new MojoFailureException("Source directory is not a directory");
        }
        
        if(!outputDirectory.exists()){
            if(!outputDirectory.mkdirs()){
                throw new MojoFailureException("Unable to create target directory");
            }
        } else if(!outputDirectory.isDirectory()){
            throw new MojoFailureException("Output directory is not a directory");
        }
        
        // =====================================================================
        // Artifact creation
        // =====================================================================
        File target = new File(outputDirectory, outputFileName);
        createZipFile(target);
        
        if("zip".equals(project.getPackaging())){
            project.getArtifact().setFile(target);
        } else if(attachArtifact){
            getLog().info("Attaching artifact...");
            projectHelper.attachArtifact(project, "zip", target);
        }
        
        getLog().info("Zip file created.");
    }

    private void createZipFile(File target) throws MojoExecutionException {
        getLog().debug("Creating zip file object...");
        
        if(target.exists()){
            getLog().debug("Deleting previously created archive");
            try {
                FileUtils.delete(target);
            } catch (IOException ex) {
                throw new MojoExecutionException("I/O Exception while deleting old generated artifact", ex);
            }
        }
        
        getLog().debug("Initializing zip file creator");
        ZipFile zipFile = new ZipFile(target);
        try {
            getLog().debug("Adding zip content...");
            for(File f : sourceDirectory.listFiles()){
                if(f.isDirectory()){
                    zipFile.addFolder(f);
                } else {
                    zipFile.addFile(f);
                }
            }
        } catch (ZipException ex) {
            throw new MojoExecutionException("Zip Exception while adding source directory", ex);
        } finally {
            getLog().debug("Closing zip file...");
            try {
                zipFile.close();
            } catch (IOException ex) {
                throw new MojoExecutionException("I/O Exception while closing zip", ex);
            }
        }
    }
}
