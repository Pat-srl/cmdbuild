/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cad.dxfparser;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import java.io.InputStreamReader;
import static java.lang.Math.toRadians;
import static java.util.Collections.unmodifiableList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import java.util.function.Consumer;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.cmdbuild.utils.cad.CadGeometryUtils.arcFromBulge;
import org.cmdbuild.utils.cad.dxfparser.model.DxfArc;
import org.cmdbuild.utils.cad.dxfparser.model.DxfDocument;
import org.cmdbuild.utils.cad.dxfparser.model.DxfEntity;
import org.cmdbuild.utils.cad.dxfparser.model.DxfGenericObject;
import org.cmdbuild.utils.cad.dxfparser.model.DxfObject;
import org.cmdbuild.utils.cad.dxfparser.model.DxfPolyline;
import org.cmdbuild.utils.cad.dxfparser.model.DxfValue;
import org.cmdbuild.utils.cad.dxfparser.model.DxfValueImpl;
import org.cmdbuild.utils.cad.dxfparser.model.DxfVariable;
import org.cmdbuild.utils.cad.dxfparser.model.DxfVariableImpl;
import org.cmdbuild.utils.cad.dxfparser.model.DxfVertex;
import org.cmdbuild.utils.cad.model.CadPoint;
import static org.cmdbuild.utils.cad.model.CadPoint.point;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.toDouble;
import static org.cmdbuild.utils.lang.CmConvertUtils.toInt;
import static org.cmdbuild.utils.lang.CmMapUtils.MapDuplicateKeyMode.ALLOW_DUPLICATES;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DxfReader {

    private final static String BLOCK_CODE = "CM_BLOCK_CODE";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Stack<Consumer<DxfStreamEvent>> eventHandler = new Stack<>();

    private final Multimap<String, DxfVariable> headerVariables = LinkedHashMultimap.create();
    private final List<DxfEntity> entities = list();
    private final List<DxfObject> objects = list();

    private final Map<String, List<DxfEntity>> blocks = map();

    private int lineNumber = 0;

    public DxfReader() {
        eventHandler.push(this::handleDefault);
    }

    public DxfDocument readStream(InputStreamReader reader) {
        new DxfStreamProcessor(this::handleEvent).processStream(reader);
        DxfDocument document = new DxfDocumentImpl();
        logger.debug("parsed dxf document, version = {}", document.getAcadVersion());
        return document;
    }

    private void handleEvent(DxfStreamEvent event) {
        lineNumber = event.getLineNumber();
        eventHandler.peek().accept(event);
    }

    private void pushHandler(Consumer<DxfStreamEvent> handler) {
        logger.trace("switch handler after line {}", lineNumber);
        eventHandler.push(handler);
    }

    private void replaceHandler(Consumer<DxfStreamEvent> handler) {
        logger.trace("switch handler after line {}", lineNumber);
        eventHandler.pop();
        eventHandler.push(handler);
    }

    private void popHandler() {
        logger.trace("switch handler after line {}", lineNumber);
        eventHandler.pop();
    }

    private void bubbleHandler(DxfStreamEvent event) {
        logger.trace("switch handler at line {}", lineNumber);
        eventHandler.pop();
        eventHandler.peek().accept(event);
    }

    private boolean getAngDirClockwise() {
        return Optional.ofNullable(Iterables.getOnlyElement(headerVariables.get("$ANGDIR"), null)).map(v -> toInt(v.getStringValue()) == 1).orElse(false);
    }

    private double getAngBase() {
        return Optional.ofNullable(Iterables.getOnlyElement(headerVariables.get("$ANGBASE"), null)).map(v -> toDouble(v.getStringValue()) + 90).orElse(90d);
    }

    private void handleDefault(DxfStreamEvent event) {
        switch (event.getGroupCodeDashValue()) {
            case "0-SECTION" ->
                pushHandler(this::handleSection);
        }
    }

    private void handleSection(DxfStreamEvent event) {
        switch (event.getGroupCodeDashValue()) {
            case "2-HEADER" ->
                replaceHandler(new HeaderHandler());
            case "2-ENTITIES" ->
                replaceHandler(new EntitiesHandler());
            case "2-TABLES" ->
                replaceHandler(new TablesHandler());
            case "2-OBJECTS" ->
                replaceHandler(new ObjectsHandler());
            case "2-BLOCKS" ->
                replaceHandler(new BlocksHandler());
            case "0-ENDSEC" ->
                popHandler();
        }
    }

    private DxfValueImpl createValue(DxfStreamEvent event) {
        String value = event.getValue().trim();//TODO check trim
        return new DxfValueImpl(event.getGroupCode(), value);
    }

    private class HeaderHandler implements Consumer<DxfStreamEvent> {

        private DxfVariableImpl variable;

        public HeaderHandler() {
            logger.debug("processing dxf header");
        }

        @Override
        public void accept(DxfStreamEvent event) {
            switch (event.getGroupCode()) {
                case 0 -> {
                    //ENDSEC
                    logger.debug("found {} header variables", headerVariables.size());
                    popHandler();
                }
                case 9 -> {
                    variable = new DxfVariableImpl(event.getValue());
                    headerVariables.put(variable.getKey(), variable);
                }
                default ->
                    variable.addValue(createValue(event));
            }
        }

    }

    private class EntitiesHandler implements Consumer<DxfStreamEvent> {

        public EntitiesHandler() {
            logger.debug("processing dxf entities starting with line {}", lineNumber);
        }

        @Override
        public void accept(DxfStreamEvent event) {
            switch (event.getGroupCode()) {
                case 0 -> {
                    switch (event.getValue()) {
                        case "ENDSEC" -> {
                            logger.debug("end of entities section at line {}, found {} entities", lineNumber, entities.size());
                            popHandler();
                        }
                        case "LINE" ->
                            pushHandler(new LineHandler(entities::add));
                        case "ARC" ->
                            pushHandler(new ArcHandler(entities::add));
                        case "CIRCLE" ->
                            pushHandler(new CircleHandler(entities::add));
//                        case "ELLIPSE" -> // TODO
//                            pushHandler(new EllipseHandler(entities::add));
                        case "POLYLINE" ->
                            pushHandler(new PolylineHandler(entities::add));
                        case "LWPOLYLINE" ->
                            pushHandler(new LwpolylineHandler(entities::add));
//                        case "TEXT" -> // TODO
//                            pushHandler(new TextHandler(entities::add));
//                        case "HATCH" -> // TODO
//                            pushHandler(new HatchHandler(entities::add)); TODO test this
                        case "INSERT" ->
                            pushHandler(new BlockReferenceHandler());
                    }
                }
            }
        }
    }

    private class BlocksHandler implements Consumer<DxfStreamEvent> {

        public BlocksHandler() {
            logger.info("processing dxf blocks starting with line {}", lineNumber);
        }

        @Override
        public void accept(DxfStreamEvent event) {
            switch (event.getGroupCode()) {
                case 0 -> {
                    switch (event.getValue()) {
                        case "ENDSEC" -> {
                            logger.info("end of blocks section at line {}, found {} blocks", lineNumber, blocks.size());
                            popHandler();
                        }
                        case "BLOCK" ->
                            pushHandler(new BlockHandler());
                    }
                }
            }
        }
    }

    private class BlockHandler implements Consumer<DxfStreamEvent> {

        private String blockName, blockLayer;
        private final List<DxfEntity> blockEntities = list();

        public BlockHandler() {
        }

        @Override
        public void accept(DxfStreamEvent event) {
            switch (event.getGroupCode()) {
                case 8 -> {
                    if (isBlank(blockLayer)) {
                        blockLayer = event.getValue();
                    }
                }
                case 2 -> {
                    if (isBlank(blockName)) {
                        blockName = event.getValue();
                        logger.info("found block with name =< {} >", blockName);
                    }
                }
                case 0 -> {
                    switch (event.getValue()) {
                        case "ENDBLK" -> {
                            logger.info("end of block with code =< {} > content = {} entities", blockName, blockEntities.size());
                            blocks.put(checkNotBlank(blockName), ImmutableList.copyOf(blockEntities));
                            popHandler();
                        }
                        case "LINE" ->
                            pushHandler(new LineHandler(blockEntities::add));
                        case "ARC" ->
                            pushHandler(new ArcHandler(blockEntities::add));
                        case "CIRCLE" ->
                            pushHandler(new CircleHandler(blockEntities::add));
//                        case "ELLIPSE" -> // TODO
//                            pushHandler(new EllipseHandler(blockEntities::add));
                        case "POLYLINE" ->
                            pushHandler(new PolylineHandler(blockEntities::add));
                        case "LWPOLYLINE" ->
                            pushHandler(new LwpolylineHandler(blockEntities::add));
                    }
                }
            }
        }
    }

    private class TablesHandler implements Consumer<DxfStreamEvent> {

        public TablesHandler() {
            logger.debug("processing dxf tables");
        }

        @Override
        public void accept(DxfStreamEvent event) {
            switch (event.getGroupCode()) {
                case 0 -> {
                    switch (event.getValue()) {
                        case "ENDSEC" ->
                            popHandler();
                    }
                }
            }
        }
    }

    private class ObjectsHandler implements Consumer<DxfStreamEvent> {

        public ObjectsHandler() {
            logger.debug("processing dxf objects");
        }

        @Override
        public void accept(DxfStreamEvent event) {
            switch (event.getGroupCode()) {
                case 0 -> {
                    switch (event.getValue()) {
                        case "ENDSEC" ->
                            popHandler();
                        case "GEODATA", "ACAD_PROXY_OBJECT" ->
                            pushHandler(new GenericObjectHandler(event.getValue()));
                    }
                }
            }
        }
    }

    private class GenericObjectHandler implements Consumer<DxfStreamEvent> {

        private final DxfGenericObjectImpl obj;

        public GenericObjectHandler(String type) {
            obj = new DxfGenericObjectImpl(type);
            logger.debug("processing object of type =< {} >", type);
            objects.add(obj);
        }

        @Override
        public void accept(DxfStreamEvent event) {
            switch (event.getGroupCode()) {
                case 0 ->
                    bubbleHandler(event);
                default ->
                    obj.addValue(createValue(event));
            }
        }

    }

    private class BlockReferenceHandler implements Consumer<DxfStreamEvent> {

        private Double x, y;
        private Double rotation = 0.0;
        private Double xscale = 1.0, yscale = 1.0;
        private String layer, blockName;
        private final DxfExtendedDataImpl xdata = new DxfExtendedDataImpl();

        public BlockReferenceHandler() {
        }

        @Override
        public void accept(DxfStreamEvent event) {
            switch (event.getGroupCode()) {
                case 0 -> {
                    List<DxfEntity> blockEntities = checkNotNull(blocks.get(blockName), "block not found for name =< {} >", blockName);
                    CadPoint offset = point(x, y);
                    if (blockEntities.isEmpty()) {
                        logger.info("skip empty entity block =< {} > layer =< {} >", blockName, layer);
                    } else {
                        logger.info("add {} entities from block =< {} > with scale =< {} {} > with rotation =< {} > with offset =< {} > layer =< {} >", blockEntities.size(), blockName, xscale, yscale, rotation, offset, layer);
                        List<DxfEntity> convertedBlockEntities = blockEntities.stream().map(e -> {
                            if (e instanceof DxfPolyline dxfPolyline) {
                                return DxfPolylineImpl.copyOf(dxfPolyline).withLayer(layer).withScale(xscale, yscale).withRotation(rotation).withOffset(offset).build();
                            } else if (e instanceof DxfArc dxfArc) {
                                return DxfArcImpl.copyOf(dxfArc).withLayer(layer).withScale(xscale, yscale).withRotation(rotation).withOffset(offset).build();
                            }
                            return e;
                        }).collect(toList());
                        convertedBlockEntities.forEach(entities::add);
                        DxfEntitySetImpl entitySet = new DxfEntitySetImpl(layer, convertedBlockEntities, xdata);
                        entities.add(entitySet);
                    }
                    bubbleHandler(event);
                }
                case 2 -> {
                    logger.info("found block reference (INSERT) for block =< {} >", event.getValue());
                    blockName = event.getValue();
                }
                case 5 -> {
                    logger.debug("entity {}", event.getValue());
                }
                case 8 ->
                    layer = event.getValue();
                case 10 ->
                    x = event.getValueAsDouble();
                case 20 ->
                    y = event.getValueAsDouble();
                case 41 -> {
                    logger.debug("found X scale factor of {} ", event.getValue());
                    xscale = event.getValueAsDouble();
                }
                case 42 -> {
                    logger.debug("found Y scale factor of {} ", event.getValue());
                    yscale = event.getValueAsDouble();
                }
                case 50 -> {
                    logger.info("found block rotation of {} ", event.getValue());
                    if (equal(event.getValueAsDouble(), 0.0)) {
                        rotation = 0.0;
                    } else {
                        rotation = toRadians(event.getValueAsDouble());
                    }
                }
                case 70 ->
                    logger.info("found reference block id {}", event.getValue());
                case 330 ->
                    logger.debug("owner of the entity and a central DXF structure {}", event.getValue());
                case 1001 ->
                    pushHandler(new XdataHandler(event.getValue(), xdata));
            }
        }
    }

    private class TextHandler implements Consumer<DxfStreamEvent> {

//        private final Consumer<DxfPolilyne> callback;
//        private final List<Double> xpoints = list(), ypoints = list();
//        public TextHandler(Consumer<DxfPolilyne> callback) {
//            this.callback = checkNotNull(callback);
//        }
        @Override
        public void accept(DxfStreamEvent event) {
            switch (event.getGroupCode()) {
                case 0 -> // TODO
                    bubbleHandler(event);
//                case 8:
//                    polyLine.setLayer(event.getValue());
//                    break;
//                case 10:
//                    xpoints.add(event.getValueAsDouble());
//                    break;
//                case 20:
//                    ypoints.add(event.getValueAsDouble());
//                    break;
//                case 70:
//                    int polylineFlag = event.getValueAsInt();
//                    switch (polylineFlag) {
//                        case 1:
//                            polyLine.setClosedPerimeter(true);
//                            break;
//                    }
//                    break;
//                case 1001:
//                    pushHandler(new XdataHandler(event.getValue(), polyLine.getXdata()));
//                    break;
            }
        }
    }

    private class LwpolylineHandler implements Consumer<DxfStreamEvent> {

        private DxfPolylineImpl polyLine;
        private final List<Double> xpoints = list(), ypoints = list();
        private final Map<Integer, Double> bulges = map();
        private String layer;
        private boolean closedPerimeter = false;

        private final Consumer<DxfPolyline> callback;

        public LwpolylineHandler(Consumer<DxfPolyline> callback) {
            polyLine = DxfPolylineImpl.builder().withXdata(new DxfExtendedDataImpl()).build();
            this.callback = callback;
        }

        @Override
        public void accept(DxfStreamEvent event) {
            switch (event.getGroupCode()) {
                case 0 -> {
                    checkArgument(xpoints.size() == ypoints.size());
                    for (int i = 0; i < xpoints.size(); i++) {
                        DxfVertex point = new DxfVertexImpl(xpoints.get(i), ypoints.get(i));
                        double bulge = bulges.getOrDefault(i, 0d);
                        if (bulge == 0d) {
                            polyLine.addVertex(point);
                        } else {
                            checkArgument(i <= xpoints.size() || polyLine.isClosedPerimeter(), "invalid nonzero bulge for last vertext of non-closed polilyne!");
                            DxfVertex nextPoint = new DxfVertexImpl(xpoints.get((i + 1) % xpoints.size()), ypoints.get((i + 1) % xpoints.size()));
                            arcFromBulge(point, nextPoint, bulge).forEach(polyLine::addVertex);
                        }
                    }
                    polyLine = DxfPolylineImpl.copyOf(polyLine).withLayer(layer).withClosedPerimeter(closedPerimeter).build();
                    callback.accept(polyLine);
                    bubbleHandler(event);
                }
                case 8 ->
                    layer = event.getValue();
                case 10 ->
                    xpoints.add(event.getValueAsDouble());
                case 20 ->
                    ypoints.add(event.getValueAsDouble());
                case 42 ->
                    bulges.put(xpoints.size() - 1, event.getValueAsDouble());
                case 70 -> {
                    int polylineFlag = event.getValueAsInt();
                    switch (polylineFlag) {
                        case 1 ->
                            closedPerimeter = true;
                    }
                }
                case 1001 ->
                    pushHandler(new XdataHandler(event.getValue(), polyLine.getXdata()));
            }
        }
    }

    private class LineHandler implements Consumer<DxfStreamEvent> {

        private DxfPolylineImpl polyLine;
        private Double x1, y1, x2, y2;
        private String layer;
        private boolean closedPerimeter = false;

        private final Consumer<DxfPolyline> callback;

        public LineHandler(Consumer<DxfPolyline> callback) {
            polyLine = polyLine = DxfPolylineImpl.builder().withXdata(new DxfExtendedDataImpl()).build();
            this.callback = checkNotNull(callback);
        }

        @Override
        public void accept(DxfStreamEvent event) {
            switch (event.getGroupCode()) {
                case 0 -> {
                    polyLine.addVertex(new DxfVertexImpl(x1, y1));
                    polyLine.addVertex(new DxfVertexImpl(x2, y2));
                    polyLine = DxfPolylineImpl.copyOf(polyLine).withLayer(layer).withClosedPerimeter(closedPerimeter).build();
                    callback.accept(polyLine);
                    bubbleHandler(event);
                }
                case 8 ->
                    layer = event.getValue();
                case 10 ->
                    x1 = event.getValueAsDouble();
                case 20 ->
                    y1 = event.getValueAsDouble();
                case 11 ->
                    x2 = event.getValueAsDouble();
                case 21 ->
                    y2 = event.getValueAsDouble();
                case 70 -> {
                    int polylineFlag = event.getValueAsInt();
                    switch (polylineFlag) {
                        case 1 ->
                            closedPerimeter = true;
                    }
                }
                case 1001 ->
                    pushHandler(new XdataHandler(event.getValue(), polyLine.getXdata()));
            }
        }
    }

    private class HatchHandler implements Consumer<DxfStreamEvent> {

        private DxfPolylineImpl polyLine;
        private Double x, y;
        private Integer vertexNum, hatchStyle, hatchPattern;
        private String layer;
        private boolean closedPerimeter = false;

        public HatchHandler(Consumer<DxfPolyline> callback) {
            polyLine = polyLine = DxfPolylineImpl.builder().withXdata(new DxfExtendedDataImpl()).build();
            callback.accept(polyLine);
        }

        @Override
        public void accept(DxfStreamEvent event) {
            switch (event.getGroupCode()) {
                case 0 -> {
                    polyLine.addVertex(new DxfVertexImpl(x, y));
                    polyLine = DxfPolylineImpl.copyOf(polyLine).withLayer(layer).withClosedPerimeter(closedPerimeter).build();
                    bubbleHandler(event);
                }
                case 8 ->
                    layer = event.getValue();
                case 10 ->
                    x = event.getValueAsDouble();
                case 20 -> {
                    y = event.getValueAsDouble();
                    if (vertexNum != null && vertexNum > 0) {
                        polyLine.addVertex(new DxfVertexImpl(x, y));
                        x = null;
                        y = null;
                        vertexNum--;
                    }
                }
                case 93 ->
                    vertexNum = event.getValueAsInt();
                case 70 -> {
                    int polylineFlag = event.getValueAsInt();
                    switch (polylineFlag) {
                        case 1 ->
                            closedPerimeter = true;
                    }
                }
                case 75 ->
                    hatchStyle = event.getValueAsInt();
//                    0 = Hatch “odd parity” area (Normal style)
//1 = Hatch outermost area only (Outer style)
//2 = Hatch through entire area (Ignore style)
                case 76 ->
                    hatchPattern = event.getValueAsInt();
                // 0 = User-defined; 1 = Predefined; 2 = Custom
                case 1001 ->
                    pushHandler(new XdataHandler(event.getValue(), polyLine.getXdata()));
            }
        }
    }

    private class ArcHandler implements Consumer<DxfStreamEvent> {

        private DxfArcImpl arc;
        private final DxfExtendedDataImpl xdata = new DxfExtendedDataImpl();
        private Double startAngle, endAngle, radius, x, y;
        private String layer;

        private final Consumer<DxfArc> callback;

        public ArcHandler(Consumer<DxfArc> callback) {
            arc = DxfArcImpl.builder().withXdata(new DxfExtendedDataImpl()).build();
            this.callback = checkNotNull(callback);
        }

        @Override
        public void accept(DxfStreamEvent event) {
            switch (event.getGroupCode()) {
                case 0 -> {
                    //TODO check this, last element ??
                    arc = DxfArcImpl.copyOf(arc).withCenter(new CadPoint(x, y)).withLayer(layer).withRadius(radius).withStartAngle(getStartAngle()).withEndAngle(getEndAngle()).build();
                    callback.accept(arc);
                    bubbleHandler(event);
                }
                case 8 ->
                    layer = event.getValue();
                case 10 ->
                    x = event.getValueAsDouble();
                case 20 ->
                    y = event.getValueAsDouble();
                case 40 ->
                    radius = event.getValueAsDouble();
                case 50 ->
                    startAngle = event.getValueAsDouble();
                case 51 ->
                    endAngle = event.getValueAsDouble();
                case 1001 ->
                    pushHandler(new XdataHandler(event.getValue(), xdata));
            }
        }

        private double getStartAngle() {
            double angle = getAngDirClockwise() ? startAngle : endAngle;
            return getAngBase() + (getAngDirClockwise() ? angle : -angle);
        }

        private double getEndAngle() {
            double angle = getAngDirClockwise() ? endAngle : startAngle;
            return getAngBase() + (getAngDirClockwise() ? angle : -angle);
        }
    }

    private class CircleHandler implements Consumer<DxfStreamEvent> {

        private final DxfExtendedDataImpl xdata = new DxfExtendedDataImpl();
        private Double radius, x, y;
        private String layer;

        private final Consumer<DxfArc> callback;

        public CircleHandler(Consumer<DxfArc> callback) {
            this.callback = checkNotNull(callback);
        }

        @Override
        public void accept(DxfStreamEvent event) {
            switch (event.getGroupCode()) {
                case 0 -> {
                    //TODO check this, last element ??
                    callback.accept(new DxfArcImpl(point(x, y), 0d, 360d, radius, layer, xdata));
                    bubbleHandler(event);
                }
                case 8 ->
                    layer = event.getValue();
                case 10 ->
                    x = event.getValueAsDouble();
                case 20 ->
                    y = event.getValueAsDouble();
                case 40 ->
                    radius = event.getValueAsDouble();
                case 1001 ->
                    pushHandler(new XdataHandler(event.getValue(), xdata));
            }
        }
    }

    private class EllipseHandler implements Consumer<DxfStreamEvent> {

        private final DxfExtendedDataImpl xdata = new DxfExtendedDataImpl();
        private Double startAngle, endAngle, radius, cx, cy, cz, mx, my, mz;
        private Boolean counterClockwise;
        private String layer;

        private final Consumer<DxfArc> callback;

        public EllipseHandler(Consumer<DxfArc> callback) {
            this.callback = checkNotNull(callback);
        }

        @Override
        public void accept(DxfStreamEvent event) {
            switch (event.getGroupCode()) {
                case 0 -> {
                    //TODO check this, last element ??
                    callback.accept(DxfArcImpl.builder()
                            .withCenter(point(cx, cy))
                            .withStartAngle(startAngle)
                            .withEndAngle(endAngle)
                            .withRadius(radius)
                            .withLayer(layer)
                            .withXdata(xdata)
                            .build()
                    );
                    bubbleHandler(event);
                }
                case 8 ->
                    layer = event.getValue();
                case 10 ->
                    cx = event.getValueAsDouble();
                case 20 ->
                    cy = event.getValueAsDouble();
                case 30 ->
                    cz = event.getValueAsDouble();
                case 11 ->
                    mx = event.getValueAsDouble();
                case 21 ->
                    my = event.getValueAsDouble();
                case 31 ->
                    mz = event.getValueAsDouble();
                case 40 ->
                    radius = event.getValueAsDouble();
                case 41 ->
                    startAngle = event.getValueAsDouble();
                case 42 ->
                    endAngle = event.getValueAsDouble();
                case 73 ->
                    counterClockwise = event.getValueAsBoolean();
                case 1001 ->
                    pushHandler(new XdataHandler(event.getValue(), xdata));
            }
        }
    }

    private class PolylineHandler implements Consumer<DxfStreamEvent> {

        private DxfPolylineImpl polyLine;
        private String layer;
        private boolean closedPerimeter = false;

        private final Consumer<DxfPolyline> callback;

        public PolylineHandler(Consumer<DxfPolyline> callback) {
            polyLine = DxfPolylineImpl.builder().withXdata(new DxfExtendedDataImpl()).build();
            this.callback = callback;
        }

        @Override
        public void accept(DxfStreamEvent event) {
            switch (event.getGroupCode()) {
                case 0 -> {
                    switch (event.getValue()) {
                        case "VERTEX" ->
                            pushHandler(new PolylineVertexHandler());
                        default -> {
                            polyLine = DxfPolylineImpl.copyOf(polyLine).withLayer(layer).withClosedPerimeter(closedPerimeter).build();
                            callback.accept(polyLine);
                            bubbleHandler(event);
                        }
                    }
                }
                case 8 ->
                    layer = event.getValue();
                case 70 -> {
                    int polylineFlag = event.getValueAsInt();
                    switch (polylineFlag) {
                        case 1 ->
                            closedPerimeter = true;
                    }
                }
                case 1001 ->
                    pushHandler(new XdataHandler(event.getValue(), polyLine.getXdata()));
            }
        }

        private class PolylineVertexHandler implements Consumer<DxfStreamEvent> {

            private DxfVertexImpl vertex;
            private double x, y, z;

            public PolylineVertexHandler() {
                vertex = DxfVertexImpl.builder().build();
            }

            @Override
            public void accept(DxfStreamEvent event) {
                switch (event.getGroupCode()) {
                    case 0 -> {
                        switch (event.getValue()) {
                            case "SEQEND" ->
                                popHandler();
                            default -> {
                                vertex = DxfVertexImpl.copyOf(vertex).withX(x).withY(y).withZ(z).build();
                                polyLine.addVertex(vertex);
                                bubbleHandler(event);
                            }
                        }
                    }
                    case 10 ->
                        x = event.getValueAsDouble();
                    case 20 ->
                        y = event.getValueAsDouble();
                    case 30 ->
                        z = event.getValueAsDouble();
                }

            }
        }
    }

    private class XdataHandler implements Consumer<DxfStreamEvent> {

        private final DxfExtendedDataImpl xdata;
        private String appName;

        private XdataHandler(String value, DxfExtendedDataImpl xdata) {
            this.appName = value;
            this.xdata = checkNotNull(xdata);
        }

        @Override
        public void accept(DxfStreamEvent event) {
            switch (event.getGroupCode()) {
                case 1002 -> {
                }
                case 1001 -> //TODO verify this
                    appName = event.getValue();
                case 1000 ->
                    xdata.addXdata(appName, event.getValue());
                case 0 ->
                    bubbleHandler(event);
            }
            //TODO
            //TODO process other xdata values
        }
    }

    private class DxfDocumentImpl implements DxfDocument {

        private final Map<String, DxfVariable> headerVariables;
        private final List<DxfEntity> entities;
        private final List<DxfObject> objects;
//        private final DxfGeoreferenceInfo georeferenceInfo;

        private DxfDocumentImpl() {
            this.headerVariables = DxfReader.this.headerVariables.values().stream().collect(toMap(DxfVariable::getKey, identity(), ALLOW_DUPLICATES)).accept(m -> {
                Iterator<DxfVariable> iterator = DxfReader.this.headerVariables.values().iterator();
                DxfVariable head = null;
                while (iterator.hasNext()) {
                    DxfVariable element = iterator.next();
                    switch (element.getKey()) {
                        case "$CUSTOMPROPERTYTAG" ->
                            head = element;
                        case "$CUSTOMPROPERTY" -> {
                            DxfVariable var = new DxfVariableImpl(checkNotNull(head, "invalid custom property structure/sequence").getStringValue(), element.getValues().values());
                            m.put(var.getKey(), var);
                            head = null;
                        }
                    }
                }

            });
            this.entities = ImmutableList.copyOf(DxfReader.this.entities);
            this.objects = ImmutableList.copyOf(DxfReader.this.objects);
//            this.georeferenceInfo = buildGeoreferenceInfoSafe();
        }

        @Override
        public Map<String, DxfVariable> getHeaderVariables() {
            return headerVariables;
        }

        @Override
        public List<DxfEntity> getEntities() {
            return entities;
        }

        @Override
        public List<DxfObject> getObjects() {
            return objects;
        }

//        @Override
//        public DxfGeoreferenceInfo getGeoreferenceInfo() {
//            return georeferenceInfo;
//        }
    }

    private static class DxfGenericObjectImpl implements DxfGenericObject {

        private final String type;
        private final List<DxfValue> values;

        public DxfGenericObjectImpl(String type) {
            this.type = checkNotBlank(type);
            this.values = list();
        }

        @Override
        public String getType() {
            return type;
        }

        @Override
        public List<DxfValue> getValues() {
            return unmodifiableList(values);
        }

        public void addValue(DxfValueImpl value) {
            values.add(value);
//            checkArgument(values.put(value.getGroupCode(), value) == null, "duplicate value received for groupCode = {}", value.getGroupCode());
        }

        @Override
        public String toString() {
            return "DxfGenericObject{values=" + values + '}';
        }

    }
}
