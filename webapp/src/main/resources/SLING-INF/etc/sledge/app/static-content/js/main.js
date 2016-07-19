(function (global, $, app) {

	/**
	 * The LoadConfigButton loads the selected config file from the list.
	 * The config file content is loaded from a service, which returns the content as string.
	 *
	 * @param selector - The button itself.
	 * @param $envFileContent - The element where the environment file content is displayed.
	 * @param $environmentFileList - The list element containing all the config files.
	 */
	app.LoadConfigButton = function (selector, $environmentFileList, $envFileContent) {

		// Private variables and functions
		var $button = $(selector);
		var resourcePath = $environmentFileList.attr("data-package-resource-path");

		var bindClick = function () {
			$button.click(function () {
				loadConfigFileContent();
			});
		};

		var bindChange = function () {
			$environmentFileList.change(function () {
				loadConfigFileContent();
			});
		}

		var getConfigFileLoadUrl = function () {
			var envName= $environmentFileList.find(":selected").val();
			return resourcePath + ".config.html?environmentName=" + envName;
		};

		var loadConfigFileContent = function () {
			$.ajax(getConfigFileLoadUrl())
				.done(function (response) {
					$envFileContent.val(response);
				})
				.fail(function () {
					alert("Error occured during loading configuration file, please check logs!");
				});
		};

		var init = function () {
			bindClick();
			bindChange();
		};

		// Public API
		return {
			init: init
		};

	};


})(window, jQuery, window.Sledge || (window.Sledge = {}));

// Application startup
$(document).ready(function () {

	// Package view
	var $environmentFileList = $("select#environment-file-list");
	var $envFileContent = $("textarea#environment-file-content");
	var loadConfigButton = new Sledge.LoadConfigButton("button.js-load-config-button", $environmentFileList, $envFileContent);


	loadConfigButton.init();
});