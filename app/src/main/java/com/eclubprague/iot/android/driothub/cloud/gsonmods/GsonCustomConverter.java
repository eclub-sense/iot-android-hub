package com.eclubprague.iot.android.driothub.cloud.gsonmods;

import org.restlet.data.MediaType;
import org.restlet.engine.resource.VariantInfo;
import org.restlet.ext.gson.GsonConverter;
import org.restlet.ext.gson.GsonRepresentation;
import org.restlet.representation.Representation;

/**
 * Created by Dat on 28.7.2015.
 */
public class GsonCustomConverter extends GsonConverter {

    /** Variant with media type application/json. */
    private static final VariantInfo VARIANT_JSON = new VariantInfo(
            MediaType.APPLICATION_JSON);

    @Override
    protected <T> GsonRepresentation<T> create(T source) {
        return new GsonCustomRepresentation<T>(source);
    }

    @Override
    protected <T> GsonRepresentation<T> create(Representation source, Class<T> objectClass) {
        return new GsonCustomRepresentation<T>(source, objectClass);
    }

}
