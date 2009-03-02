package vuzeplugins.llj;

import org.gudy.azureus2.plugins.PluginException;
import org.gudy.azureus2.plugins.PluginInterface;
import org.gudy.azureus2.plugins.UnloadablePlugin;
import org.gudy.azureus2.plugins.config.ConfigParameter;
import org.gudy.azureus2.plugins.config.ConfigParameterListener;
import org.gudy.azureus2.plugins.download.Download;
import org.gudy.azureus2.plugins.download.DownloadCompletionListener;
import org.gudy.azureus2.plugins.ui.model.BasicPluginConfigModel;

public class AutoShutdownPlugin implements UnloadablePlugin {

	@Override
	public void initialize(final PluginInterface pluginInterface)
			throws PluginException {
		BasicPluginConfigModel configModel = pluginInterface.getUIManager()
				.createBasicPluginConfigModel("azautoshutdown");
		configModel.addBooleanParameter2("enableAutoShutdown",
				"enableAutoShutdown", false).addConfigParameterListener(
				new ConfigParameterListener() {

					@Override
					public void configParameterChanged(ConfigParameter param) {
						boolean enableAutoShutdown = pluginInterface
								.getPluginconfig().getPluginBooleanParameter(
										"enableAutoShutdown");
						DownloadCompletionListener downloadCompletionListener = new DownloadCompletionListener() {

							@Override
							public void onCompletion(Download d) {
								Download[] downloads = pluginInterface
										.getDownloadManager().getDownloads();
								boolean allFinished = true;
								for (Download downloadi : downloads) {
									if (!downloadi.isComplete()) {
										allFinished = false;
										return;
									}
								}
								if (allFinished) {
									if (pluginInterface.getUtilities()
											.isWindows()) {
										try {
											pluginInterface.getUtilities()
													.createProcess(
															"shutdown -s");
										} catch (PluginException e) {
											e.printStackTrace();
										}
									} else if (pluginInterface.getUtilities()
											.isLinux()) {
										try {
											pluginInterface
													.getUtilities()
													.createProcess(
															"gnome-power-cmd.sh shutdown");
										} catch (PluginException e) {
											e.printStackTrace();
										}
									}
								}

							}
						};
						if (enableAutoShutdown) {
							pluginInterface.getDownloadManager()
									.getGlobalDownloadEventNotifier()
									.addCompletionListener(
											downloadCompletionListener);
						} else {
							pluginInterface.getDownloadManager()
									.getGlobalDownloadEventNotifier()
									.removeCompletionListener(
											downloadCompletionListener);
						}
					}

				});
		pluginInterface.getPluginconfig().setPluginParameter(
				"enableAutoShutdown", false);
	}

	@Override
	public void unload() throws PluginException {

	}

}
