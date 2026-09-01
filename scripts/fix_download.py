#!/usr/bin/env python3
"""Tiflogram: yuklash tugashi tovushi."""
import sys
MODE = sys.argv[1] if len(sys.argv) > 1 else "check"

FIXES = []

FIXES.append({
    "id": 10,
    "label": "DownloadController: fileLoaded -> tovush",
    "path": "TMessagesProj/src/main/java/org/telegram/messenger/DownloadController.java",
    "old": (
        "        } else if (id == NotificationCenter.fileLoaded || id == NotificationCenter.httpFileDidLoad) {\n"
        "            listenerInProgress = true;\n"
        "            String fileName = (String) args[0];"
    ),
    "new": (
        "        } else if (id == NotificationCenter.fileLoaded || id == NotificationCenter.httpFileDidLoad) {\n"
        "            listenerInProgress = true;\n"
        "            String fileName = (String) args[0];\n"
        "            // Tiflogram: foydalanuvchi yuklamasi tugaganda tovush\n"
        "            boolean tiflogramIsUserDownload = false;\n"
        "            try {\n"
        "                for (int _i = 0; _i < downloadingFiles.size(); _i++) {\n"
        "                    MessageObject _mo = downloadingFiles.get(_i);\n"
        "                    if (_mo == null) continue;\n"
        "                    String _fn = _mo.getFileName();\n"
        "                    if (_fn != null && fileName != null && (_fn.equals(fileName) || fileName.endsWith(_fn))) {\n"
        "                        tiflogramIsUserDownload = true;\n"
        "                        break;\n"
        "                    }\n"
        "                    if (_mo.getDocument() != null) {\n"
        "                        String _an = FileLoader.getAttachFileName(_mo.getDocument());\n"
        "                        if (_an != null && _an.equals(fileName)) {\n"
        "                            tiflogramIsUserDownload = true;\n"
        "                            break;\n"
        "                        }\n"
        "                    }\n"
        "                }\n"
        "            } catch (Throwable ignore) {}\n"
        "            if (tiflogramIsUserDownload) {\n"
        "                try {\n"
        "                    android.media.MediaPlayer mp = android.media.MediaPlayer.create(\n"
        "                            ApplicationLoader.applicationContext,\n"
        "                            org.telegram.messenger.R.raw.tiflogram_dl_done);\n"
        "                    if (mp != null) {\n"
        "                        mp.setOnCompletionListener(android.media.MediaPlayer::release);\n"
        "                        mp.start();\n"
        "                    }\n"
        "                } catch (Throwable ignore) {}\n"
        "            }"
    ),
})

FIXES.append({
    "id": 11,
    "label": "DownloadController.onDownloadComplete -> tovush",
    "path": "TMessagesProj/src/main/java/org/telegram/messenger/DownloadController.java",
    "old": (
        "    public void onDownloadComplete(MessageObject parentObject) {\n"
        "        if (parentObject == null || parentObject.getDocument() == null) {\n"
        "            return;\n"
        "        }\n"
        "        TLRPC.Document document = parentObject.getDocument();"
    ),
    "new": (
        "    public void onDownloadComplete(MessageObject parentObject) {\n"
        "        if (parentObject == null || parentObject.getDocument() == null) {\n"
        "            return;\n"
        "        }\n"
        "        // Tiflogram: yuklash tugadi tovushi\n"
        "        try {\n"
        "            android.media.MediaPlayer mp = android.media.MediaPlayer.create(\n"
        "                    ApplicationLoader.applicationContext,\n"
        "                    org.telegram.messenger.R.raw.tiflogram_dl_done);\n"
        "            if (mp != null) {\n"
        "                mp.setOnCompletionListener(android.media.MediaPlayer::release);\n"
        "                mp.start();\n"
        "            }\n"
        "        } catch (Throwable ignore) {}\n"
        "        TLRPC.Document document = parentObject.getDocument();"
    ),
})


def read_file(path):
    try:
        with open(path, "r", encoding="utf-8") as f:
            return f.read()
    except FileNotFoundError:
        return None


def main():
    if MODE not in ("check", "apply"):
        print("Noma'lum rejim:", MODE)
        sys.exit(1)
    print("=== Rejim:", MODE, "(yuklash tovushi) ===\n")
    hato = 0
    for fix in FIXES:
        path = fix["path"]
        content = read_file(path)
        if content is None:
            print("XATO fayl yo'q:", path)
            hato = 1
            continue
        if fix["old"] not in content:
            print("XATO topilmadi:", fix["label"])
            hato = 1
            continue
        print("OK:", fix["label"])
        if MODE == "apply":
            content = content.replace(fix["old"], fix["new"], 1)
            with open(path, "w", encoding="utf-8") as f:
                f.write(content)
    if hato:
        sys.exit(1)
    print("Tayyor.")


if __name__ == "__main__":
    main()
