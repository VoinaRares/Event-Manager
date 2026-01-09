import { CommonModule, Location } from '@angular/common';
import { Component, Input, OnInit, inject, signal, HostListener } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { EventDto } from '../domain/EventDto';
import { EventService } from '../api/event.service';
import { ImageService } from '../api/image.service';

@Component({
  selector: 'app-event-detail',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './event-detail.component.html',
  styleUrl: './event-detail.component.css'
})
export class EventDetailComponent implements OnInit {
  @Input() event?: EventDto | null;
  private readonly location = inject(Location);
  private readonly route = inject(ActivatedRoute);
  private readonly eventService = inject(EventService);
  private readonly imageService = inject(ImageService);

  loading = false;
  error: string | null = null;
  readonly uploading = signal(false);

  ngOnInit(): void {
    const paramId = this.route.snapshot.paramMap.get('eventId');
    const queryId = this.route.snapshot.queryParamMap.get('id');
    const stateEvent = (window.history.state && (window.history.state as any).event) ?? null;

    const idStr = paramId ?? queryId ?? (stateEvent ? String((stateEvent as any).id) : null) ?? (this.event ? String(this.event.id) : null);

    if (idStr) {
      const id = Number(idStr);
      if (!Number.isNaN(id)) {
        this.fetchEvent(id);
        return;
      }
    }

    if (this.event && this.event.id) {
      this.fetchEvent(this.event.id);
      return;
    }

    this.error = 'No event id provided.';
  }

  private fetchEvent(id: number): void {
    this.loading = true;
    this.error = null;
    this.eventService.getById(id).subscribe({
      next: (e) => {
        this.event = e;
        this.loading = false;
      },
      error: () => {
        this.error = 'Unable to load event details.';
        this.loading = false;
      }
    });
  }

  goBack(): void {
    this.location.back();
  }

  mapLink(e: EventDto | null): string | null {
    if (!e || e.latitude == null || e.longitude == null) return null;
    return `https://www.google.com/maps/search/?api=1&query=${e.latitude},${e.longitude}`;
  }

  onFileSelected(files: FileList | null): void {
    if (!files || !files.length) return;
    const file = files.item(0);
    if (!file || !this.event) return;
    if (!this.event.id) return;

    this.uploading.set(true);
    this.imageService.upload(this.event.id, file).subscribe({
      next: () => {
        this.uploading.set(false);
        this.fetchEvent(this.event!.id!);
      },
      error: () => {
        this.uploading.set(false);
      }
    });
  }

  // -- Lightbox viewer state & controls -------------------------------------------------
  viewerOpen = false;
  selectedIndex = 0;

  openViewer(index: number) {
    if (!this.event?.images || !this.event.images.length) return;
    this.selectedIndex = index;
    this.viewerOpen = true;
  }

  closeViewer() {
    this.viewerOpen = false;
  }

  prev() {
    if (!this.event?.images || !this.event.images.length) return;
    const len = this.event.images.length;
    this.selectedIndex = (this.selectedIndex - 1 + len) % len;
  }

  next() {
    if (!this.event?.images || !this.event.images.length) return;
    const len = this.event.images.length;
    this.selectedIndex = (this.selectedIndex + 1) % len;
  }

  openOriginal(event?: Event) {
    event?.stopPropagation();
    const url = this.event?.images?.[this.selectedIndex]?.url;
    if (url) window.open(url, '_blank');
  }

  @HostListener('window:keydown', ['$event'])
  handleKeyDown(event: KeyboardEvent) {
    if (!this.viewerOpen) return;
    if (event.key === 'Escape') this.closeViewer();
    if (event.key === 'ArrowLeft') this.prev();
    if (event.key === 'ArrowRight') this.next();
  }
}
