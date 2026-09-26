import xml.etree.ElementTree as ET
import sys

def get_keys(path):
    tree = ET.parse(path)
    return {el.get('name') for el in tree.getroot().findall('string')}

en = get_keys('shared/src/commonMain/composeResources/values/strings.xml')
ru = get_keys('shared/src/commonMain/composeResources/values-ru/strings.xml')

only_en = en - ru
only_ru = ru - en

if only_en:
    print(f"Только в EN: {only_en}")
if only_ru:
    print(f"Только в RU: {only_ru}")

if not only_en and not only_ru:
    print("Паритет OK")
else:
    sys.exit(1)