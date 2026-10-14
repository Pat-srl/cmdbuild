/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.common.utils;

import com.google.common.base.Function;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import org.cmdbuild.auth.user.UserData;
import org.cmdbuild.auth.user.UserRepository;
import org.cmdbuild.auth.utils.AuthUtils;
import org.cmdbuild.dao.config.inner.Patch;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.AttributeImpl;
import org.cmdbuild.gis.CmGeometry;
import org.cmdbuild.gis.GisValue;
import org.cmdbuild.gis.Linestring;
import org.cmdbuild.gis.Point;
import org.cmdbuild.gis.Polygon;
import static org.cmdbuild.utils.hash.CmHashUtils.hash;
import static org.cmdbuild.utils.lang.CmCollectionUtils.listOf;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmExceptionUtils.illegalArgument;
import org.cmdbuild.utils.lang.CmMapUtils;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.key;

public class WsSerializationUtils {

    private WsSerializationUtils() {
        // utility class
    }

    public static CmMapUtils.FluentMap<String, Object> serializePatchInfo(Patch patch) {
        return map("name", patch.getVersion(),
                "description", patch.getDescription(),
                "category", String.valueOf(patch.getCategory()));
    }

    public static CmMapUtils.FluentMap serializeGeoValue(GisValue<?> value) {
        return map(
                "_id", hash(key(value.getOwnerClassId(), value.getLayerName(), Long.toString(value.getOwnerCardId()))),
                "_type", value.getType().name().toLowerCase(),
                "_attr", value.getLayerName(),
                "_owner_type", value.getOwnerClassId(),
                "_owner_id", value.getOwnerCardId(),
                "_owner_description", value.getOwnerCardDescription()
        ).with(serializeGeometry(value.getGeometry()));
    }

    public static CmMapUtils.FluentMap serializeGeometry(CmGeometry value) {
        return switch (value.getType()) {
            case POINT ->
                map("x", value.as(Point.class).getX(), "y", value.as(Point.class).getY());
            case LINESTRING ->
                map("points", value.as(Linestring.class).getPoints().stream().map(p -> map("x", p.getX(), "y", p.getY())).toList());
            case POLYGON ->
                map("points", value.as(Polygon.class).getPoints().stream().map(p -> map("x", p.getX(), "y", p.getY())).toList());
            default ->
                throw illegalArgument("unsupported geometry type = %s", value.getType());
        };
    }

    @Nullable
    public static String userDescription(UserRepository userRepository, String user) {
        return Optional.ofNullable(trimToNull(user)).map(AuthUtils::getUsernameFromHistoryUser).map(userRepository::getUserDataByUsernameOrNull).map(UserData::getDescription).orElse(user);
    }

    public static List<Attribute> prepareAttributesToUpdateForOrder(Function<String, Attribute> attributeFun, List<String> attributes) {
        checkArgument(set(attributes).size() == attributes.size(), "duplicate found =< %s >", attributes);
        AtomicInteger index = new AtomicInteger(0);
        return listOf(Attribute.class).accept(ordered -> attributes.forEach(attr -> {
            Attribute attribute = checkNotNull(attributeFun.apply(attr), "attribute =< %s > does not exist", attr);
            if (attribute.getIndex() != index.incrementAndGet()) {
                ordered.add(AttributeImpl.copyOf(attribute).withIndex(index.get()).build());
            }
        }));
    }
}
