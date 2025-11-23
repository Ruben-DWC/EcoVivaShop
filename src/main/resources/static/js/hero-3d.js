// hero-3d.js - Minimal three.js scene to render a low-poly rotating eco-sphere
// Fallbacks included and gentle motion for performance
(() => {
    // Ensure three.js is available
    function loadScript(url) {
        return new Promise((resolve, reject) => {
            const s = document.createElement('script');
            s.src = url;
            s.onload = resolve;
            s.onerror = reject;
            document.head.appendChild(s);
        });
    }

    // Start the 3D scene
    async function init() {
        try {
            if (!window.THREE) {
                await loadScript('https://cdn.jsdelivr.net/npm/three@0.153.0/build/three.min.js');
            }
            const THREE = window.THREE;
            const canvas = document.getElementById('heroCanvas');
            if (!canvas) return;

            const scene = new THREE.Scene();

            const renderer = new THREE.WebGLRenderer({ canvas, antialias: true, alpha: true });
            renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2));
            renderer.setSize(canvas.clientWidth, canvas.clientHeight, false);
            renderer.shadowMap.enabled = false;

            const camera = new THREE.PerspectiveCamera(45, canvas.clientWidth / canvas.clientHeight, 0.1, 100);
            camera.position.set(0, 0, 5);

            // Simple geometry: low poly sphere
            const geometry = new THREE.IcosahedronGeometry(1.4, 1);
            // Material with gradient effect via vertex colors
            const material = new THREE.MeshStandardMaterial({
                color: 0x1db954,
                roughness: 0.4,
                metalness: 0.2,
                flatShading: true,
                emissive: 0x053414,
            });

            const sphere = new THREE.Mesh(geometry, material);
            sphere.rotation.x = -0.2;
            scene.add(sphere);

            // add a subtle ring or halo using torus geometry
            const ringGeo = new THREE.TorusGeometry(2.4, 0.02, 16, 100);
            const ringMat = new THREE.MeshBasicMaterial({ color: 0x0f9d58, transparent: true, opacity: 0.08 });
            const ring = new THREE.Mesh(ringGeo, ringMat);
            ring.rotation.x = 1.3;
            ring.position.set(0.1, -0.4, -0.5);
            scene.add(ring);

            // Lights
            const ambient = new THREE.AmbientLight(0xffffff, 0.5);
            scene.add(ambient);
            const directional = new THREE.DirectionalLight(0xffffff, 0.6);
            directional.position.set(5, 10, 7.5);
            scene.add(directional);

            // Resize handler
            function resize() {
                if (!canvas) return;
                const width = canvas.clientWidth;
                const height = canvas.clientHeight;
                renderer.setSize(width, height, false);
                camera.aspect = width / height;
                camera.updateProjectionMatrix();
            }

            // Parallax on mouse move for interactivity
            let mouseX = 0, mouseY = 0;
            window.addEventListener('mousemove', (e) => {
                const w = window.innerWidth;
                const h = window.innerHeight;
                mouseX = (e.clientX - w / 2) / w;
                mouseY = (e.clientY - h / 2) / h;
            });

            // Pause when not visible
            let animateId;
            const obs = new IntersectionObserver(entries => {
                entries.forEach(entry => {
                    if (entry.isIntersecting) {
                        animate();
                    } else {
                        cancelAnimationFrame(animateId);
                    }
                });
            }, { threshold: 0.1 });
            obs.observe(canvas);

            let t = 0;
            function animate() {
                t += 0.01;
                // Rotations
                sphere.rotation.y += 0.005 + Math.abs(mouseX) * 0.03;
                sphere.rotation.x += 0.002 + Math.abs(mouseY) * 0.02;
                sphere.position.x = mouseX * 0.6;
                sphere.position.y = -mouseY * 0.3;

                // subtle breathing
                sphere.scale.setScalar(1 + Math.sin(t) * 0.01);
                ring.rotation.z += 0.002;

                renderer.render(scene, camera);
                animateId = requestAnimationFrame(animate);
            }

            // start
            resize();
            animate();

            // Listen for resizes
            window.addEventListener('resize', resize);

            // Stop when page hidden to save CPU
            document.addEventListener('visibilitychange', () => {
                if (document.hidden) {
                    cancelAnimationFrame(animateId);
                } else {
                    animate();
                }
            });
        } catch (err) {
            console.warn('3D hero initialization failed:', err);
        }
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
})();
