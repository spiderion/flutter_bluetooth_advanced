import 'dart:convert';
import 'dart:typed_data';

class MapConverter {
  Map<String, dynamic>? getMapDataFromBytes(List<int> bytes) {
    String result = utf8.decode(bytes);
    Map<String, dynamic>? mapData;
    try {
      mapData = jsonDecode(result);
      return mapData;
    } catch (e) {
      print("can't convert bluetooth data object + $result");
      return null;
    }
  }

  Map<String, dynamic>? getMapDataUint8List(Uint8List uint8list) {
    return getMapDataFromBytes(uint8list.toList());
  }

  Uint8List getUint8ListFromMapData(Map<dynamic, dynamic> map) {
    final result = utf8.encode(jsonEncode(map));
    return Uint8List.fromList(result);
  }
}
