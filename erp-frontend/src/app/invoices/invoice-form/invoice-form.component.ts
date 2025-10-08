import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, FormArray, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { InvoiceService } from '../invoice.service';
import { ProductService } from '../../products/product.service';
import { CustomerService } from '../../customers/customer.service';

@Component({
  selector: 'app-invoice-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatSelectModule
  ],
  templateUrl: './invoice-form.component.html',
  styleUrl: './invoice-form.component.scss'
})
export class InvoiceFormComponent implements OnInit {
  form: FormGroup;
  isEdit = false;
  invoiceId: number | null = null;
  loading = false;
  error: string | null = null;

  products: any[] = [];
  customers: any[] = [];

  constructor(
    private fb: FormBuilder,
    private invoiceService: InvoiceService,
    private productService: ProductService,
    private customerService: CustomerService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.form = this.fb.group({
      number: ['', [Validators.required]],
      date: ['', [Validators.required]],
      customer: ['', [Validators.required]],
      isActive: [true],
      lines: this.fb.array([])
    });
  }

  ngOnInit() {
    this.productService.getProducts().subscribe(products => this.products = products);
    this.customerService.getCustomers().subscribe(customers => this.customers = customers);
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEdit = true;
      this.invoiceId = +id;
      this.loading = true;
      this.invoiceService.getInvoice(this.invoiceId).subscribe({
        next: invoice => {
          this.form.patchValue({
            number: invoice.number,
            date: invoice.date,
            customer: invoice.customer?.id,
            isActive: invoice.isActive
          });
          this.lines.clear();
          if (invoice.lines) {
            invoice.lines.forEach((line: any) => {
              this.lines.push(this.fb.group({
                product: [line.product?.id, Validators.required],
                quantity: [line.quantity, [Validators.required, Validators.min(1)]],
                price: [line.price, [Validators.required, Validators.min(0.01)]]
              }));
            });
          }
          this.loading = false;
        },
        error: err => {
          this.error = 'No se pudo cargar la factura';
          this.loading = false;
        }
      });
    } else {
      this.isEdit = false;
      this.addLine();
    }
  }

  get lines(): FormArray {
    return this.form.get('lines') as FormArray;
  }

  addLine() {
    this.lines.push(this.fb.group({
      product: [null, Validators.required],
      quantity: [1, [Validators.required, Validators.min(1)]],
      price: [0, [Validators.required, Validators.min(0.01)]]
    }));
  }

  removeLine(index: number) {
    this.lines.removeAt(index);
  }

  save() {
    if (this.form.invalid) return;
    this.loading = true;
    const formValue = this.form.value;
    const invoiceData = {
      number: formValue.number,
      date: formValue.date,
      customer: { id: formValue.customer },
      isActive: formValue.isActive,
      lines: formValue.lines.map((line: any) => ({
        product: { id: line.product },
        quantity: line.quantity,
        price: line.price
      }))
    };
    if (this.isEdit && this.invoiceId) {
      this.invoiceService.updateInvoice(this.invoiceId, invoiceData).subscribe({
        next: () => this.router.navigate(['/invoices']),
        error: err => {
          this.error = 'Error al actualizar factura';
          this.loading = false;
        }
      });
    } else {
      this.invoiceService.createInvoice(invoiceData).subscribe({
        next: () => this.router.navigate(['/invoices']),
        error: err => {
          this.error = 'Error al crear factura';
          this.loading = false;
        }
      });
    }
  }

  cancel() {
    this.router.navigate(['/invoices']);
  }
}
