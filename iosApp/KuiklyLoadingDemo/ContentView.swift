import SwiftUI
import KuiklyLoadingShared

struct ContentView: View {
    var body: some View {
        KuiklyRenderViewPage(
            pageName: "LoadingGalleryPage",
            data: [:]
        )
        .ignoresSafeArea()
    }
}
