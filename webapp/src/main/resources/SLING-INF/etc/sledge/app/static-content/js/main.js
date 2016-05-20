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
		var $element = $(selector);
		var resourcePath = $element.attr("data-load-url");

		var bindClick = function () {
			$element.click(function () {
				loadConfigFileContent();
			});
		};

		var getConfigFileLoadUrl = function () {
			var envFilename = $environmentFileList.find(":selected").val();
			return resourcePath + ".config.html?environmentFilename=" + envFilename;
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
		};

		// Public API
		return {
			init: init
		};

	};


})(window, jQuery, window.MyApp || (window.MyApp = {}));

// Application startup
$(document).ready(function () {

	// Package view
	var $environmentFileList = $("select#environment-file-list");
	var $envFileContent = $("textarea#environment-file-content");
	var loadConfigButton = new MyApp.LoadConfigButton("button.js-load-config-button", $environmentFileList, $envFileContent);

	loadConfigButton.init();
});