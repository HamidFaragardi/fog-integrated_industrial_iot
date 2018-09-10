import shutil
for i in range(100):
    shutil.copy(f"{i}.txt",f"{i+300}.txt")
    shutil.copy(f"{i}.txt",f"{i+400}.txt")