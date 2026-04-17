document.addEventListener("DOMContentLoaded", () => {
	const rolePage = document.querySelector(".sidebar") || document.title.includes("Setup");
	if (!rolePage) {
		return;
	}

	const layer = document.createElement("div");
	layer.className = "blood-rain";

	const dropCount = window.innerWidth < 768 ? 14 : 30;
	for (let i = 0; i < dropCount; i += 1) {
		const drop = document.createElement("span");
		drop.className = "blood-drop";

		const left = Math.random() * 100;
		const size = 7 + Math.random() * 12;
		const duration = 6.2 + Math.random() * 7.4;
		const delay = Math.random() * 12;
		const drift = -36 + Math.random() * 72;

		drop.style.left = `${left}%`;
		drop.style.setProperty("--drop-size", `${size}px`);
		drop.style.setProperty("--fall-duration", `${duration}s`);
		drop.style.setProperty("--fall-delay", `-${delay}s`);
		drop.style.setProperty("--drift", `${drift}px`);

		layer.appendChild(drop);
	}

	document.body.appendChild(layer);
});
