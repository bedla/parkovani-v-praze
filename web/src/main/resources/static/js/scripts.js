$(document).ready(function () {
    $("#progress").hide();

    var vectorSource = new ol.source.Vector({wrapX: false}),
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
                vectorLayer
            ],
            view: new ol.View({
                center: ol.proj.transform([14.3980231, 50.0682351], 'EPSG:4326', 'EPSG:3857'),
                zoom: 12
            })
        }),
        drawPoint = function (wktPoint, icon, opacity) {

            //var point = new ol.geom.Point(new ol.format.WKT().readFeature(wktPoint));
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
            vectorSource.addFeatures([pointFeature]);

        },
        drawPath = function (pointsAjax) {

            var line = new ol.geom.LineString(pointsAjax);

            line.transform('EPSG:4326', 'EPSG:3857');

            var styleFunction = function (feature, resolution) {
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


            var lineFeature = new ol.Feature({geometry: line});
            vectorLayer.setStyle(styleFunction);
            vectorSource.addFeatures([lineFeature]);

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
                    return;
                }

                for (var i = 0; i < data.length; i++) {
                    drawPath(decodePath(data[i].points));

                    drawPoint(data[i].targetPoint, 'parking-meter', 1);
                }

                drawPoint(data[0].sourcePoint, 'car', 1);

            }).fail(function (jqXHR, textStatus) {
                $("#progress").hide();
                alert(textStatus);
            });
        }
        ;

    map.on('click', function (evt) {
        var coordinate = evt.coordinate;
        callRoute(coordinate[0], coordinate[1]);
    });

});


function decodePath(encoded, is3D) {
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
}
