import os
import glob
from PIL import Image

source = r"C:\Users\P K PRANAV\.gemini\antigravity\brain\629f441f-7129-41a7-9b04-24d9c69b9a5d\sentinel_twin_app_logo_1787658541769.jpg"
res_dir = r"C:\prototype\SentinelTwin\app\src\main\res"

sizes = {
    "mdpi": 48,
    "hdpi": 72,
    "xhdpi": 96,
    "xxhdpi": 144,
    "xxxhdpi": 192
}

img = Image.open(source)

for density, size in sizes.items():
    resized = img.resize((size, size), Image.Resampling.LANCZOS)
    
    folder = os.path.join(res_dir, f"mipmap-{density}")
    os.makedirs(folder, exist_ok=True)
    
    # Delete old webp/png files
    for f in glob.glob(os.path.join(folder, "ic_launcher*")):
        os.remove(f)
        
    # Save new PNGs
    resized.save(os.path.join(folder, "ic_launcher.png"))
    resized.save(os.path.join(folder, "ic_launcher_round.png"))

# Delete anydpi xml files that override the PNGs
anydpi = os.path.join(res_dir, "mipmap-anydpi-v26")
if os.path.exists(anydpi):
    for f in glob.glob(os.path.join(anydpi, "*.xml")):
        os.remove(f)

print("Icons generated.")
