import { Component, OnInit } from '@angular/core'
import { ActivatedRoute, ParamMap } from '@angular/router'
import { ILearningPath } from '../../models/academy-learning-paths.model'
import { DomSanitizer, SafeStyle } from '@angular/platform-browser'

@Component({
  selector: 'ws-app-learning-path-details',
  templateUrl: './learning-path-details.component.html',
  styleUrls: ['./learning-path-details.component.scss'],
})
export class LearningPathDetailsComponent implements OnInit {

  learningPath: ILearningPath | null = null
  banner = '/assets/common/general-banner/banner.jpg'
  bannerUrl: SafeStyle | null = null
  constructor(
    private route: ActivatedRoute,
    private sanitizer: DomSanitizer,
  ) { }

  ngOnInit() {
    if (this.route.parent) {
      this.route.parent.data.subscribe(data => {
        this.route.paramMap.subscribe((params: ParamMap) => {
          const id = params.get('id')
          if (data.pageData.data.learningPaths) {
            this.learningPath = data.pageData.data.learningPaths.find((lp: ILearningPath) => lp.id === id)
          }
        })
      })
    }

    this.bannerUrl = this.sanitizer.bypassSecurityTrustStyle(
      `url(${this.banner})`,
    )
  }

}
