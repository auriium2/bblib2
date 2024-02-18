import { ExtensionContext } from "@foxglove/studio";
import { initTuningPanel } from "./ConfigTunePanel";

export type structything = {
  u: num;
};

export function activate(extensionContext: ExtensionContext): void {
  extensionContext.registerPanel({ name: "mattlib2-configPanel", initPanel: initTuningPanel });
  extensionContext.registerMessageConverter<structything>({
    converter: (structything) => {
      return { u: structything.u };
    },
    fromSchemaName: "structything",
    toSchemaName: "structything",
  });
}
