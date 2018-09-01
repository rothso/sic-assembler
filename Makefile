.PHONY: all, clean
.SECONDARY: kotlinc

all: symbol-table.jar

symbol-table.jar: kotlinc
	@echo -e "\e[32mCompiling with kotlinc\e[0m"
	@kotlinc/bin/kotlinc src/main/* -include-runtime -d symbol-table.jar
	@echo -e "Done! Run with: \e[36mjava -jar symbol-table.jar the_input_file.txt\e[0m"

kotlinc:
	@echo -e "\e[32mDownloading Kotlin 1.2.61\e[0m"
	@curl -O -L https://github.com/JetBrains/kotlin/releases/download/v1.2.61/kotlin-compiler-1.2.61.zip
	@unzip kotlin-compiler-1.2.61.zip
	@rm kotlin-compiler-1.2.61.zip

clean:
	@$(RM) symbol-table.jar
	@$(RM) -r kotlinc