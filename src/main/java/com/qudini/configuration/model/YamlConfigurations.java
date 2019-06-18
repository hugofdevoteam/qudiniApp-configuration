package com.qudini.configuration.model;

import lombok.Data;

@Data
public class YamlConfigurations {

    private Meta meta;

    private QudiniAppStaticData qudiniAppStaticData;

    private RequestStaticValues requestStaticValues;

}
