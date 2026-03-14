SHELL := /bin/sh

.DEFAULT_GOAL := help

GRADLEW ?= ./gradlew
ANDROID_APP_MODULE ?= androidApp
COMPOSE_MODULE ?= composeApp
IOS_PROJECT ?= iosApp/iosApp.xcodeproj
ADB ?= adb
ANDROID_APP_ID ?= com.example.demo_03
ANDROID_MAIN_ACTIVITY ?= .MainActivity
ANDROID_APK_DIR ?= $(ANDROID_APP_MODULE)/build/outputs/apk/debug

.PHONY: help gradle-tasks clean test verify android-build android-launch desktop-run desktop-package desktop-package-dmg desktop-package-msi desktop-package-deb ios-open

help: ## Show available commands
	@printf "Common commands for Demo03\n\n"
	@grep -E '^[a-zA-Z0-9_.-]+:.*## ' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*## "}; {printf "  %-22s %s\n", $$1, $$2}'

gradle-tasks: ## List Gradle tasks
	$(GRADLEW) tasks

clean: ## Clean Gradle build outputs
	$(GRADLEW) clean

test: ## Run shared tests
	$(GRADLEW) :$(COMPOSE_MODULE):allTests

verify: ## Run lint, compile, and shared tests
	$(GRADLEW) verify

android-build: ## Build Android debug APK, install it with adb, and launch the app
	$(GRADLEW) :$(ANDROID_APP_MODULE):assembleDebug
	@apk_path=$$(find $(ANDROID_APK_DIR) -type f -name '*.apk' | head -n 1); \
	if [ -z "$$apk_path" ]; then \
		echo "No APK found under $(ANDROID_APK_DIR)"; \
		exit 1; \
	fi; \
	echo "Installing $$apk_path"; \
	$(ADB) install -r "$$apk_path"; \
	echo "Launching $(ANDROID_APP_ID)/$(ANDROID_MAIN_ACTIVITY)"; \
	$(ADB) shell am start -n $(ANDROID_APP_ID)/$(ANDROID_MAIN_ACTIVITY)

android-launch: ## Install the built Android debug APK with adb and launch the app
	@apk_path=$$(find $(ANDROID_APK_DIR) -type f -name '*.apk' | head -n 1); \
	if [ -z "$$apk_path" ]; then \
		echo "No APK found under $(ANDROID_APK_DIR). Run 'make android-build' first."; \
		exit 1; \
	fi; \
	echo "Installing $$apk_path"; \
	$(ADB) install -r "$$apk_path"; \
	echo "Launching $(ANDROID_APP_ID)/$(ANDROID_MAIN_ACTIVITY)"; \
	$(ADB) shell am start -n $(ANDROID_APP_ID)/$(ANDROID_MAIN_ACTIVITY)

desktop-run: ## Run the desktop app
	$(GRADLEW) :$(COMPOSE_MODULE):run

desktop-package: ## Build all configured desktop packages
	$(GRADLEW) :$(COMPOSE_MODULE):packageDistributionForCurrentOS

desktop-package-dmg: ## Build macOS DMG package
	$(GRADLEW) :$(COMPOSE_MODULE):packageDmg

desktop-package-msi: ## Build Windows MSI package
	$(GRADLEW) :$(COMPOSE_MODULE):packageMsi

desktop-package-deb: ## Build Debian package
	$(GRADLEW) :$(COMPOSE_MODULE):packageDeb

ios-open: ## Open the iOS project in Xcode
	open $(IOS_PROJECT)
