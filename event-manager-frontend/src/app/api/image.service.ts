import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ImageDto } from '../domain/ImageDto';

@Injectable({ providedIn: 'root' })
export class ImageService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8080/api/images';

  upload(eventId: number, file: File): Observable<ImageDto> {
    const form = new FormData();
    form.append('file', file, file.name);
    const params = new HttpParams().set('eventId', String(eventId));
    return this.http.post<ImageDto>(`${this.baseUrl}/upload`, form, { params });
  }

  create(payload: Partial<ImageDto>): Observable<ImageDto> {
    return this.http.post<ImageDto>(this.baseUrl, payload);
  }

  list(): Observable<ImageDto[]> {
    return this.http.get<ImageDto[]>(this.baseUrl);
  }

  delete(id: number) {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }
}