import { Component, OnInit, Input, ElementRef, ViewChild, OnChanges, AfterViewInit } from '@angular/core'
import { Chart } from 'chart.js'
@Component({
  selector: 'ws-app-line-graph',
  templateUrl: './line-graph.component.html',
  styleUrls: ['./line-graph.component.scss'],
})
export class LineGraphComponent implements OnInit, AfterViewInit, OnChanges {
  lineChart: Chart | null = null
  i = 0
  @Input() lineData1: any
  @ViewChild('chartContainer', { static: true }) chartContainer: ElementRef<HTMLDivElement> | null = null
  constructor() { }

  ngOnInit() { }
  ngAfterViewInit() {
    this.onLineGraphCreate()
  }

  ngOnChanges() {
    setTimeout(() => {
      this.onLineGraphCreate()
    },
      // tslint:disable-next-line:align
      1000)
  }

  onLineGraphCreate() {
    const monthList = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']
    const currentMonth = new Date().getMonth() + 1
    let month: string[] = []
    if (currentMonth >= 4) {
      month = monthList.slice(currentMonth - 4, currentMonth)
    } else {
      const previousMonth = monthList.slice(currentMonth - 4)
      const nextMonth = monthList.slice(0, currentMonth)
      month = previousMonth.concat(nextMonth)
    }
    const canvas = document.createElement('canvas')
    canvas.id = 'lineChart'
    const lineData = this.lineData1
    if (this.chartContainer) {
      this.chartContainer.nativeElement.innerHTML = ''
      this.chartContainer.nativeElement.appendChild(canvas)
    }
    this.lineChart = new Chart(canvas.id, {
      type: 'line',
      data: {
        labels: month,
        datasets: [
          {
            label: 'Role Quotient',
            // scaleStepWidth: 1,
            borderColor: '#2d98da',
            pointBorderColor: '#2d98da',
            pointBackgroundColor: '#2d98da',
            pointHoverBackgroundColor: '#2d98da',
            pointHoverBorderColor: '#2d98da',
            pointBorderWidth: 10,
            pointHoverRadius: 10,
            pointHoverBorderWidth: 1,
            pointRadius: [1, 1, 1, 1, -90],
            fill: false,
            lineTension: 0.1,
            borderWidth: 4,
            //  data:this.lineData,
            data: [1, 2.4, 3.6],
          },
          {
            data: [1, 2.4, 3.6, 4.5],
            // data:this.lineData1,

            lineTension: 0,
            backgroundColor: '#eb3b5a',
            borderColor: '#eb3b5a',
            fill: false,
            borderDash: [5, 5],
            pointRadius: [-90, -90, -90, 5],
          },
        ],
      },

      options: {
        responsive: true,
        legend: {
          display: false,
          // position: 'bottom',
        },
        hover: {
          mode: 'index',
        },
        scales: {
          xAxes: [
            {
              gridLines: {
                drawOnChartArea: false,
              },

              scaleLabel: {
                display: true,
                labelString: 'Month',
              },
            },
          ],
          yAxes: [
            {
              gridLines: {
                drawOnChartArea: false,
              },
              ticks: {
                beginAtZero: true,
                callback(value) {
                  if (value % 1 === 0) {
                    return value
                  }
                },
              },

              scaleLabel: {
                display: true,
                labelString: 'Role Quotient',
              },
            },
          ],
        },
        tooltips: {
          callbacks: {
            label(tooltipItem: any) {
              const label = `${tooltipItem.xLabel} :  ${lineData[tooltipItem.index].key}`
              return label
            },
          },
        },
      },
    })
  }
}
