package com.channelsoft.workattent.service.word;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.*;
import java.util.Map;

public class WordUtils {

    public static void docReplaceWithPOI(String sourcePath, String targetPath,
                                         Map<String, String> map) {
        HWPFDocument doc = null;
        try {
            InputStream inp = new FileInputStream(sourcePath);
            POIFSFileSystem fs = new POIFSFileSystem(inp);
            doc = new HWPFDocument(fs);

            Range range = doc.getRange();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                range.replaceText(entry.getKey(), entry.getValue());
            }
            inp.close();
            OutputStream os = new FileOutputStream(targetPath);
            doc.write(os);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
