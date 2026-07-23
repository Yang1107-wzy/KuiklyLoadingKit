import SwiftUI
import KuiklyLoadingShared

struct ContentView: View {
    var body: some View {
        KuiklyRenderViewPage(
            pageName: "LoadingGalleryPage",
            data: acceptancePageData
        )
        .ignoresSafeArea()
    }

    private var acceptancePageData: [AnyHashable: Any] {
        guard
            let scenarioIndex = ProcessInfo.processInfo.arguments.firstIndex(
                of: "--acceptance-scenario"
            ),
            ProcessInfo.processInfo.arguments.indices.contains(scenarioIndex + 1)
        else {
            return [:]
        }
        return [
            "acceptanceScenario":
                ProcessInfo.processInfo.arguments[scenarioIndex + 1]
        ]
    }
}
