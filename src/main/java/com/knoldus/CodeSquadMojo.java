package com.knoldus;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;

@Mojo(name = "reports")
@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(force = true)
public class CodeSquadMojo extends AbstractMojo {
    
    @Parameter(property = "filePath", required = true)
    private String[] filePaths;
    
    @Parameter(property = "projectName", required = true)
    private String projectName;
    
    @Parameter(property = "moduleName", required = true)
    private String moduleName;
    
    @Parameter(property = "registrationKey", required = true)
    private String registrationKey;
    
    @Parameter(property = "organisation", required = true)
    private String organisation;
    
    /**
     * This method executes the plugin.
     *
     * @throws MojoExecutionException
     */
    public void execute() throws MojoExecutionException {
        uploadReports(filePaths, projectName, moduleName, registrationKey, organisation);
    }
    
    /**
     * This method upload reports to CodeSquad.
     *
     * @param filePaths       Files to upload to CodeSquad.
     * @param projectName     Name of the Project Name to show on dashboard.
     * @param moduleName      Name of the Module Name to show on dashboard.
     * @param registrationKey Unique Registration Key generated from CodeSquad to upload the files.
     * @param organisation    Name of the organisation to show on dashboard.
     */
    private void uploadReports(String[] filePaths, String projectName,
                               String moduleName, String registrationKey, String organisation) {
        
        CloseableHttpClient httpclient = HttpClients.createDefault();
        for (String filePath : filePaths) {
            File file = new File(filePath);
            
            HttpEntity entity = MultipartEntityBuilder.create()
                    .addTextBody(Constants.PROJECT_NAME, projectName)
                    .addTextBody(Constants.MODULE_NAME, moduleName)
                    .addTextBody(Constants.REGISTRATION_KEY, registrationKey)
                    .addTextBody(Constants.ORGANISATION, organisation)
                    .addBinaryBody(Constants.FILE, file)
                    .build();
            HttpPut httpPut = new HttpPut(Constants.CODESQUAD_URL);
            httpPut.setEntity(entity);
            
            HttpResponse response = null;
            try {
                response = httpclient.execute(httpPut);
                
                HttpEntity result = response.getEntity();
                getLog().info("-----------------------------------");
                getLog().info(response.getStatusLine().toString());
                getLog().info("-----------------------------------");
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
}
