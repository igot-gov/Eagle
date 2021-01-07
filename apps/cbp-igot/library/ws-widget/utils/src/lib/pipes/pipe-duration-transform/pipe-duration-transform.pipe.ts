import { Pipe, PipeTransform } from '@angular/core'

@Pipe({
  name: 'pipeDurationTransform',
})
export class PipeDurationTransformPipe implements PipeTransform {

  transform(data: number, type: 'time24' | 'hms' | 'hour' | 'HMS'): any {
    if (data <= 0) {
      return ''
    }
    const h = Math.floor(data / 3600)
    const m = Math.floor((data % 3600) / 60)
    const s = Math.floor((data % 3600) % 60)
    let duration = ''
    let space = ''

    switch (type) {
      case 'time24':
        return this.defaultDuration(h, m, s)
      case 'hms':
        if (h > 0) {
          duration += type === 'hms' ? `${h}h` : `${h} hr`
        }
        if (m > 0) {
          if (h > 0) {
            space = ' '
          }
          duration += type === 'hms' ? `${space}${m}m` : `${space}${m} min`
        }
        if (s > 0 && h === 0) {
          if (m > 0) {
            space = ' '
          }
          duration += type === 'hms' ? `${space}${s}s` : `${space}${s} sec`
        }
        return duration
      case 'HMS':
        if (h > 0) {
          duration += type === 'HMS' ? `${h}Hour(s)` : `${h} Hour(s)`
        }
        if (m > 0) {
          if (h > 0) {
            space = ' '
          }
          duration += type === 'HMS' ? `${space}${m}Minutes` : `${space}${m} Minutes`
        }
        if (s > 0 && h === 0) {
          if (m > 0) {
            space = ' '
          }
          duration += type === 'HMS' ? `${space}${s}Seconds` : `${space}${s} Seconds`
        }
        return duration
      case 'hour':
        if (h === 0) {
          duration += 'less than an hour'
        }
        if (h === 1) {
          duration += `${h} hour`
        }
        if (h > 1) {
          duration += `${h} hours`
        }
        return duration
      default:
        return this.defaultDuration(h, m, s)
    }
  }

  defaultDuration(h: number, m: number, s: number) {
    let duration = ''
    duration += h > 0 ? `${h.toString().padStart(2)} h : ` : ''
    duration += m > 0 ? `${m.toString().padStart(2)} m : ` : '00 m : '
    duration += s > 0 ? `${s.toString().padStart(2)} s` : '00 s'
    return duration
  }

}
