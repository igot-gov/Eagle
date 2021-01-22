import { Component, OnInit, Input } from '@angular/core'
import { ActivatedRoute, Router } from '@angular/router'

@Component({
  selector: 'ws-app-item-tile',
  templateUrl: './item-tile.component.html',
  styleUrls: ['./item-tile.component.scss'],
})
export class ItemTileComponent implements OnInit {
  @Input() data: any = {} as any
  ref = 'home'
  topics: string[] = []
  constructor(private activated: ActivatedRoute, private route: Router) { }

  ngOnInit() { }
  isString(input: any) {
    return typeof input === 'string'
  }
  goToView() {
    try {
      this.route.navigate(
        [`/app/khub/view/${this.data.category}/${this.data.itemId}/${this.data.source}`],
        {
          relativeTo: this.activated.parent,
        },
      )
    } catch (e) {
      throw e
    }
  }
}
