import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserService } from '../api/user.service';

@Component({
  selector: 'app-user-home',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './user-home.component.html',
  styleUrls: ['./user-home.component.css']
})
export class UserHomeComponent implements OnInit {
  loading = true;
  error = '';
  pendingEvents: any[] = [];
  confirmedEvents: any[] = [];

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.userService.getEvents().subscribe({
      next: (events: any[]) => {
        this.pendingEvents = events;
        this.confirmedEvents = [];
        this.loading = false;
      },
      error: () => {
        this.error = 'An error occurred while loading events.';
        this.loading = false;
      }
    });
  }
}
