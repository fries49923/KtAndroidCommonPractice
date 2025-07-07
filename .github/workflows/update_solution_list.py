import os

# 自訂輸出的檔案名稱
parent_readme = "solution_list.md"
header = "# [Kotlin Android Common Tested Code]\n\n"

with open(parent_readme, "w", encoding="utf-8") as outfile:
    outfile.write(header)
    for folder in sorted(os.listdir(".")):
        if os.path.isdir(folder) and os.path.exists(os.path.join(folder, "README.md")):
            # 子資料夾標題 + 連結
            outfile.write(f"## [{folder}](./{folder})\n\n")
            
            # 讀取該子資料夾中的 README.md
            with open(os.path.join(folder, "README.md"), "r", encoding="utf-8") as infile:
                content = infile.read().strip()
                outfile.write(content + "\n\n")
            
            # 加入分隔 <br>
            outfile.write("<br>\n\n")
