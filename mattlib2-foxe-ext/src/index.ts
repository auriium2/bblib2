import { ExtensionContext } from "@foxglove/studio";
import { initTuningPanel } from "./ConfigTunePanel";

export function activate(extensionContext: ExtensionContext): void {
  extensionContext.registerPanel({ name: "mattlib2-configPanel", initPanel: initTuningPanel });

}
