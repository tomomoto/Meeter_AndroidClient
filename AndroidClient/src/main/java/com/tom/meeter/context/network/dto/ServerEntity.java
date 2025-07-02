package com.tom.meeter.context.network.dto;

public interface ServerEntity extends BaseEntity {

    String NAME_KEY = "name";

    String getName();

    String PHOTO_PATH_KEY = "photo_path";

    String getPhotoPath();
}
