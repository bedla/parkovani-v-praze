$(document).ready(function () {
    $("#progress").hide();

    var currentRoutes = {},
        wmsFormat = 'image/png',
        wmsBounds = [1601356.5263001898, 6451626.603157492, 1625235.2613937617, 6465680.644238415],
        vectorSource = new ol.source.Vector({wrapX: false}),
        vectorLayer = new ol.layer.Vector({
            source: vectorSource,
            style: new ol.style.Style({
                fill: new ol.style.Fill({
                    color: 'rgba(255, 255, 255, 0.2)'
                }),
                stroke: new ol.style.Stroke({
                    color: '#ffcc33',
                    width: 2
                }),
                image: new ol.style.Circle({
                    radius: 7,
                    fill: new ol.style.Fill({
                        color: '#ffcc33'
                    })
                })
            })
        }),
        wmsLayerZony = new ol.layer.Image({
            source: new ol.source.ImageWMS({
                ratio: 1,
                url: 'http://localhost:8181/geoserver/parkovani/wms',
                params: {
                    'FORMAT': wmsFormat,
                    'VERSION': '1.1.1',
                    STYLES: '',
                    LAYERS: 'parkovani:DOP_ZPS_Stani_l_mercator',
                }
            })
        }),
        wmsLayerZonyAutomaty = new ol.layer.Image({
            source: new ol.source.ImageWMS({
                ratio: 1,
                url: 'http://localhost:8181/geoserver/parkovani/wms',
                params: {
                    'FORMAT': wmsFormat,
                    'VERSION': '1.1.1',
                    STYLES: '',
                    LAYERS: 'parkovani:DOP_ZPS_Automaty_b_mercator',
                }
            })
        }),
        map = new ol.Map({
            target: 'map',
            layers: [
                new ol.layer.Tile({
                    source: new ol.source.OSM({
                        attributions: [
                            new ol.Attribution({
                                html: 'All maps &copy; ' +
                                '<a href="http://www.opencyclemap.org/">OpenCycleMap</a>'
                            }),
                            ol.source.OSM.ATTRIBUTION
                        ],
                        url: 'http://{a-c}.tile.osm.org/{z}/{x}/{y}.png'
                    })
                }),
                wmsLayerZony,
                wmsLayerZonyAutomaty,
                vectorLayer
            ],
            view: new ol.View({
                center: ol.proj.transform([14.3980231, 50.0682351], 'EPSG:4326', 'EPSG:3857'),
                zoom: 12
            })
        }),
        decodePath = function (encoded, is3D) {
            // var start = new Date().getTime();
            var len = encoded.length;
            var index = 0;
            var array = [];
            var lat = 0;
            var lng = 0;
            var ele = 0;

            while (index < len) {
                var b;
                var shift = 0;
                var result = 0;
                do {
                    b = encoded.charCodeAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                var deltaLat = ((result & 1) ? ~(result >> 1) : (result >> 1));
                lat += deltaLat;

                shift = 0;
                result = 0;
                do {
                    b = encoded.charCodeAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                var deltaLon = ((result & 1) ? ~(result >> 1) : (result >> 1));
                lng += deltaLon;

                if (is3D) {
                    // elevation
                    shift = 0;
                    result = 0;
                    do
                    {
                        b = encoded.charCodeAt(index++) - 63;
                        result |= (b & 0x1f) << shift;
                        shift += 5;
                    } while (b >= 0x20);
                    var deltaEle = ((result & 1) ? ~(result >> 1) : (result >> 1));
                    ele += deltaEle;
                    array.push([lng * 1e-5, lat * 1e-5, ele / 100]);
                } else
                    array.push([lng * 1e-5, lat * 1e-5]);
            }
            // var end = new Date().getTime();
            // console.log("decoded " + len + " coordinates in " + ((end - start) / 1000) + "s");
            return array;
        },
        createPointFeature = function (wktPoint, icon, opacity, index, type) {

            var point = new ol.format.WKT().readGeometry(wktPoint);

            var iconStyle = new ol.style.Style({
                image: new ol.style.Icon(/** @type {olx.style.IconOptions} */ ({
                    anchor: [0.5, 46],
                    anchorXUnits: 'fraction',
                    anchorYUnits: 'pixels',
                    opacity: opacity,
                    src: icon + '.png'
                }))
            });

            var pointFeature = new ol.Feature({geometry: point});
            pointFeature.setStyle(iconStyle);

            pointFeature.set('parkovani.index', index);
            pointFeature.set('parkovani.type', type);

            return pointFeature;
        },
        createPathFeature = function (pointsAjax, index, type) {

            var line = new ol.geom.LineString(pointsAjax);
            line.transform('EPSG:4326', 'EPSG:3857');

            var feature = new ol.Feature({geometry: line});
            feature.set('parkovani.index', index);
            feature.set('parkovani.type', type);
            return feature;

        },
        updateRoutes = function (fromAjax) {


            vectorSource.clear();

            if (currentRoutes.ajaxData === undefined) {
                return;
            }

            if (fromAjax) {
                currentRoutes.selectedPath = undefined;

                currentRoutes.automatFeatures = [];
                currentRoutes.pathFeatures = [];
                for (var i = 0; i < currentRoutes.ajaxData.length; i++) {
                    currentRoutes.pathFeatures.push(createPathFeature(decodePath(currentRoutes.ajaxData[i].points), i, 'path'));
                    currentRoutes.automatFeatures.push(createPointFeature(currentRoutes.ajaxData[i].targetPoint, 'parking-meter', 1, i, 'automat'));
                }

                currentRoutes.carFeature = createPointFeature(currentRoutes.ajaxData[0].sourcePoint, 'car', 1, -1, 'car')
            }

            var featuresToLayer = [];

            if (currentRoutes.selectedPath !== undefined) {
                featuresToLayer.push(currentRoutes.pathFeatures[currentRoutes.selectedPath]);
            }

            for (var i = 0; i < currentRoutes.automatFeatures.length; i++) {
                featuresToLayer.push(currentRoutes.automatFeatures[i]);
            }

            featuresToLayer.push(currentRoutes.carFeature);

            vectorSource.addFeatures(featuresToLayer);

        },
        callRoute = function (x, y) {
            $("#progress").show();

            var data = [x, y];

            data = ol.proj.transform(data, 'EPSG:3857', 'EPSG:4326');

            $.ajax({
                method: "GET",
                url: "/route",
                data: {lat: data[1], lng: data[0]}
            }).done(function (data) {
                $("#progress").hide();


                if (data.length == 0) {
                    alert("404 Route not found");
                    currentRoutes = {};
                } else {
                    currentRoutes = {ajaxData: data};
                }

                updateRoutes(true);
            }).fail(function (jqXHR, textStatus) {
                $("#progress").hide();
                alert(textStatus);
            });
        },
        styleFunction = function (feature, resolution) {
            var view = map.getView(),
                zoom = view.getZoom(),
                width = 2,
                widthMap = {};

            widthMap[14] = 3;
            widthMap[15] = 4;
            widthMap[16] = 5;
            widthMap[17] = 6;
            widthMap[18] = 7;

            if (zoom > 13 && zoom < 19) {
                width = widthMap[zoom];
            } else if (zoom >= 10) {
                width = 7.1;
            }

            var styles = [
                new ol.style.Style({
                    image: new ol.style.Circle({
                        fill: new ol.style.Fill({
                            color: 'rgba(255,255,255,0.4)'
                        }),
                        stroke: new ol.style.Stroke({
                            color: '#3399CC',
                            width: 1.25
                        }),
                        radius: 5
                    }),
                    fill: new ol.style.Fill({
                        color: 'rgba(255,255,255,0.4)'
                    }),
                    stroke: new ol.style.Stroke({
                        color: '#3399CC',
                        width: width
                    })
                })
            ];
            return styles;
        };

    vectorLayer.setStyle(styleFunction);

    updateRoutes(false);

    map.on('click', function (evt) {

        var feature = map.forEachFeatureAtPixel(evt.pixel,
            function (feature, layer) {
                return feature;
            });
        if (feature) {
            if (feature.get("parkovani.type") == "automat") {
                currentRoutes.selectedPath = parseInt(feature.get("parkovani.index"));
                updateRoutes(false);
            }
        } else {
            callRoute(evt.coordinate[0], evt.coordinate[1]);
        }


    });

});
