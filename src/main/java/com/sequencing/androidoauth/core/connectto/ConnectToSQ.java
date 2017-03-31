package com.sequencing.androidoauth.core.connectto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by omazurova on 3/23/2017.
 */

public class ConnectToSQ {

    String client_id;
    String email;

    public String getClientId() {
        return client_id;
    }

    public void setClientId(String client_id) {
        this.client_id = client_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    List<File> files = new ArrayList<>();

    public static class File{

        public String name;
        public String type;
        public String url;
        public String hashType;
        public String hashValue;
        public String size;

        public String getType() {
            return type;
        }

        public File setType(String type) {
            this.type = type;
            return this;
        }

        public String getName() {
            return name;
        }

        public File setName(String name) {
            this.name = name;
            return this;
        }

        public String getUrl() {
            return url;
        }

        public File setUrl(String url) {
            this.url = url;
            return this;
        }

        public String getHashType() {
            return hashType;
        }

        public File setHashType(String hashType) {
            this.hashType = hashType;
            return this;
        }

        public String getHashValue() {
            return hashValue;
        }

        public File setHashValue(String hashValue) {
            this.hashValue = hashValue;
            return this;
        }

        public String getSize() {
            return size;
        }

        public File setSize(String size) {
            this.size = size;
            return this;
        }
    }

    private ConnectToSQ(ConnectParametersBuilder builder) {
        client_id = builder.clientId;
        email = builder.email;
        files = builder.files;
    }

    public static class ConnectParametersBuilder{
        String clientId;
        String email;
        List<File> files = new ArrayList<>();

        public ConnectParametersBuilder(){
            this
                    .withClientId("")
                    .withEmail("")
                    .withFiles(files);
        }

        public ConnectParametersBuilder withClientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public ConnectParametersBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        public ConnectParametersBuilder withFiles(List<File> files) {
            this.files = files;
            return this;
        }

        public ConnectToSQ build() {
            return new ConnectToSQ(this);
        }
    }
}
