import SwiftUI
import KuiklyLoadingDemo

struct ContentView: View {
    var body: some View {
        KuiklyRenderViewPage(
            pageName: "LoadingGalleryPage",
            data: [:]
        )
        .ignoresSafeArea()
    }
}
