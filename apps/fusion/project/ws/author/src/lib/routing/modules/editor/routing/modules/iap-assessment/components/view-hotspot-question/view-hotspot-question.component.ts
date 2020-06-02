import { Component, OnInit, Input, AfterViewInit } from '@angular/core'

@Component({
  selector: 'ws-auth-view-hotspot-question',
  templateUrl: './view-hotspot-question.component.html',
  styleUrls: ['./view-hotspot-question.component.scss'],
})
export class ViewHotspotQuestionComponent implements OnInit, AfterViewInit {
  questionDetails!: any
  solution!: any
  imageFileBase64!: string
  previewHeight!: number
  previewWidth!: number
  xPosition!: number
  yPosition!: number
  canvasId = 'previewImage'
  @Input() data: any
  @Input() review ?= false
  @Input() qNo = '1'
  constructor() { }

  ngOnInit() {
    this.canvasId = this.canvasId + this.qNo.toString()
    this.questionDetails = this.data
    this.previewHeight = this.questionDetails.imageHeight
    this.previewWidth = this.questionDetails.imageWidth
    this.imageFileBase64 = this.questionDetails.imageUrl
    if (this.data.userSolution) {
      this.xPosition = Number(this.data.userSolution.x)
      this.yPosition = Number(this.data.userSolution.y)
    }
  }

  ngAfterViewInit() {
    this.createCanvasImage()

  }

  createCanvasImage() {
    const canvas = <any>document.getElementById(this.canvasId)
    this.solution = this.questionDetails.newSolution
    const solution = this.solution
    // var userSolution = this.data.userSolution
    if (canvas.getContext) {
      const ctx = canvas.getContext('2d')
      const xPosition = this.xPosition
      const yPosition = this.yPosition
      const review = this.review
      const img = new Image()
      img.onload = function () {
        ctx.drawImage(img, 0, 0)
        if (review) {
          const pin = { x: xPosition, y: yPosition, color: 'red' }
          ctx.save()
          ctx.translate(pin.x, pin.y)
          ctx.beginPath()
          ctx.moveTo(0, 0)
          ctx.bezierCurveTo(2, -10, -20, -25, 0, -30)
          ctx.bezierCurveTo(20, -25, -2, -10, 0, 0)
          ctx.fillStyle = pin.color
          ctx.fill()
          ctx.strokeStyle = 'black'
          ctx.lineWidth = 1.5
          ctx.stroke()
          ctx.beginPath()
          ctx.arc(0, -21, 3, 0, Math.PI * 2)
          ctx.closePath()
          ctx.fillStyle = 'black'
          ctx.fill()
          ctx.restore()
        } else {
          for (let m = 0; m < solution.length; m += 1) {
            ctx.fillStyle = 'rgba(0, 150,0, 0.5)'
            ctx.fillRect(Number(solution[m].x1),
                         Number(solution[m].y1),
                         Number(solution[m].x2) - Number(solution[m].x1),
                         Number(solution[m].y2) - Number(solution[m].y1))
          }
        }
      }
      img.src = this.data.imageUrl
    }
  }

}
