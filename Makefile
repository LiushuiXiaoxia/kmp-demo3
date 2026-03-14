SHELL := /bin/sh

.DEFAULT_GOAL := help

GRADLEW ?= ./gradlew
ANDROID_APP_MODULE ?= androidApp
COMPOSE_MODULE ?= composeApp
IOS_PROJECT ?= iosApp/iosApp.xcodeproj

.PHONY: help gradle-tasks clean test android-build android-install desktop-run desktop-package desktop-package-dmg desktop-package-msi desktop-package-deb ios-open

help: ## Show available commands
	@printf "Common commands for Demo03\n\n"
	@grep -E '^[a-zA-Z0-9_.-]+:.*## ' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*## "}; {printf "  %-22s %s\n", $$1, $$2}'

gradle-tasks: ## List Gradle tasks
	$(GRADLEW) tasks

clean: ## Clean Gradle build outputs
	$(GRADLEW) clean

test: ## Run shared tests
	$(GRADLEW) :$(COMPOSE_MODULE):allTests

android-build: ## Build Android debug APK
	$(GRADLEW) :$(ANDROID_APP_MODULE):assembleDebug

android-install: ## Install Android debug build to a connected device
	$(GRADLEW) :$(ANDROID_APP_MODULE):installDebug

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
