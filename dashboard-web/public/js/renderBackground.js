!function renderBackground(){
    let body = document.querySelector('body');
    let canvas = document.querySelector('canvas');
    let ctx = canvas.getContext('2d');

    let colors = ['#FFE74C', '#FF5964', '#35A7FF', '#FFFFFF', '#011627', '#004E64', '#00A5CF', '#9FFFCB', '#25A18E', '#E4572E', '#F3A712', '#A8C686', '#3D348B', '#7678ED', '#F7B801', '#F18701', '#00C9B1', '#005D6C', '#00043C', '#AAAAAA', '#BBBBBB', '#FFEBB7', '#BFF4ED', '#280F34', '#B30753']

    let width = canvas.width = (window.innerWidth / 2) + 100;
    let height = canvas.height = (window.innerHeight / 2) + 50;

    // CANVAS SETTINGS
    let cRect = canvas.getBoundingClientRect();
    let startx = 0;

    body.addEventListener('mousemove', (e) => {
        e.preventDefault()
        let mouseX = e.pageX;
        let mouseY = e.pageY;

        let posX = mouseX - cRect.left;
        let posY = mouseY - cRect.top;

        let b2 = new Ball(posX, posY, Math.random() * 5, Math.random() * 5, colors[Math.floor(Math.random() * colors.length)], Math.floor(Math.random() * 10));

        balls.push(b2)

    })

    body.addEventListener('touchmove', (e) => {

        let touchobj = e.changedTouches[0];
        let distX = parseInt(touchobj.clientX)
        let distY = parseInt(touchobj.clientY)

        let posX = distX - cRect.left;
        let posY = distY - cRect.top;

        let b2 = new Ball(posX, posY, Math.random() * 5, Math.random() * 5, colors[Math.floor(Math.random() * colors.length)], Math.floor(Math.random() * 10));

        balls.push(b2)


    })

    //RESIZE
    window.addEventListener('resize', (e) => {
        canvas.width = width;
        canvas.height = height;
    })

    class Ball {
        constructor(x, y, velX, velY, color, size) {
            this.x = x;
            this.y = y;
            this.velX = velX;
            this.velY = velY;
            this.color = color;
            this.size = size || 5;
        }

        draw() {
            ctx.beginPath();
            ctx.fillStyle = this.color;
            ctx.arc(this.x, this.y, this.size, 0, 2 * Math.PI);
            ctx.fill();
        }

        update() {
            if (this.x >= width || this.x <= 0) {
                this.velX = -(this.velX)
            }
            if ((this.y + this.size) >= height || this.y <= 0) {
                this.velY = -(this.velY)
            }

            this.x += this.velX;
            this.y += this.velX;

        }
    }

    let balls = []


    for (let i = 0; i < 10; i++) {
        let ball = new Ball(Math.random() * width, Math.random() * height, Math.random() * 5, Math.random() * 5, colors[Math.floor(Math.random() * colors.length)], Math.floor(Math.random() * 5))
        balls.push(ball);

    }

    function run() {
        requestAnimationFrame(run)

        ctx.globalAlpha = .8;
        ctx.fillStyle = '#FFF';
        ctx.fillRect(0, 0, width, height);

        for (let i = 0; i < balls.length; i++) {
            let b = balls[i]
            b.update()
            b.draw()
        }


    }

    run()

    // COLOR THE TITLE
    let p = document.querySelector('#title');
    let text = p.textContent;
    p.textContent = ''

    for (let i = 0; i < text.length; i++) {
        let t = text[i]
        // t.textContent = ''
        // t.style.color = colors[Math.floor(Math.random() * colors.length)]
        p.textContent += t;
        p.style.color = colors[Math.floor(Math.random() * colors.length)]
    }
}