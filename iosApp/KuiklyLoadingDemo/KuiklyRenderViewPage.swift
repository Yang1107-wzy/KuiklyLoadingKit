import SwiftUI

struct KuiklyRenderViewPage: UIViewControllerRepresentable {
    let pageName: String
    let data: [AnyHashable: Any]

    func makeUIViewController(context: Context) -> UIViewController {
        KuiklyRenderViewController(
            pageName: pageName,
            pageData: data
        )
    }

    func updateUIViewController(
        _ uiViewController: UIViewController,
        context: Context
    ) {
        // Kuikly owns its page lifecycle after creation.
    }
}
