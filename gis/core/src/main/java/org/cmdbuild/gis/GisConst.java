/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.gis;

public class GisConst {

	public static final String GIS_ATTRIBUTE_TABLE_NAME = "_GisAttribute", GIS_GEOSERVER_LAYER_TABLE_NAME = "_GisGeoserverLayer";

	public static final String ATTR_OWNER = "Owner", ATTR_VISIBILITY = "Visibility", ATTR_OWNER_CLASS = "OwnerClass", ATTR_OWNER_CARD = "OwnerCard";

	@Deprecated
	public enum LayerTableAttrs {
		CARDS_BINDING("CardsBinding"),
		DESCRIPTION("Description"),
		//		FULL_NAME("FullName"),
		GEO_SERVER_NAME("GeoServerName"),
		INDEX("Index"),
		MINIMUM_ZOOM("MinimumZoom"),
		MAXIMUM_ZOOM("MaximumZoom"),
		MAP_STYLE("MapStyle"),
		NAME("Name"),
		TYPE("Type"),
		VISIBILITY("Visibility");

		private final String name;

		LayerTableAttrs(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}
}
