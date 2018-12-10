.PHONY: all, clean
.SECONDARY: kotlinc

all: assembler.jar

assembler.jar: kotlinc
	@echo -e "\e[32mCompiling with kotlinc\e[0m"
	@kotlinc/bin/kotlinc src/main/* -include-runtime -d assembler.jar
	@echo -e "Done! Run with: \e[36mjava -jar assembler.jar the_input_file.txt\e[0m"

kotlinc:
	@echo -e "\e[32mDownloading Kotlin 1.3.11\e[0m"
	@curl -O -L https://github.com/JetBrains/kotlin/releases/download/v1.3.11/kotlin-compiler-1.3.11.zip
	@unzip kotlin-compiler-1.3.11.zip
	@$(RM) kotlin-compiler-1.3.11.zip

clean:
	@$(RM) assembler.jar
	@$(RM) -r kotlinc
	@$(RM) sharfile

sharfile: clean
	@shar * > sharfile
